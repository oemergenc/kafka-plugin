package kafkaplugin.broker

import com.intellij.ide.favoritesTreeView.FavoritesListProvider
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap
import kafkaplugin.toolwindow.KafkaBrokerListener


class KafkaBrokerManager(val myProject: Project) : ProjectComponent {

    companion object Instance {
        fun getInstance(project: Project): KafkaBrokerManager {
            return project.getComponent(KafkaBrokerManager::class.java)
        }
    }

    private val myBrokers = MultiMap.createConcurrentSet<VirtualFile, KafkaBroker>()
    private val myBrokersRootOrder = ArrayList<String>()
    private var myProviders: MutableMap<String, FavoritesListProvider> = mutableMapOf()
    private val myListeners = ContainerUtil.createLockFreeCopyOnWriteList<KafkaBrokerListener>()

    @Synchronized
    fun createNewList(listName: String) {
        myBrokersRootOrder.add(listName)
        brokerAdded(listName)
    }

    private fun brokerAdded(brokerName: String) {
        for (listener in myListeners) {
            listener.brokerAdded(brokerName)
        }
    }

    private fun brokerRemoved(listName: String) {
        for (listener in myListeners) {
            listener.brokerRemoved(listName)
        }
    }

    fun addBrokerListener(listener: KafkaBrokerListener, parent: Disposable) {
        myListeners.add(listener)
        listener.rootsChanged()
        Disposer.register(parent, Disposable { myListeners.remove(listener) })
    }

    fun getAvailableBrokerListNames(): Any {
        return ArrayList<String>(myBrokersRootOrder)
    }

    @Synchronized
    fun fireListeners(listName: String) {
        rootsChanged()
    }

    private fun rootsChanged() {
        for (listener in myListeners) {
            listener.rootsChanged()
        }
    }


}
