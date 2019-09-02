package kafkaplugin.broker

import com.intellij.ide.projectView.impl.AbstractUrl
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.Consumer
import com.intellij.util.SmartList
import com.intellij.util.TreeItem
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap
import kafkaplugin.toolwindow.KafkaBrokerListNode
import kafkaplugin.toolwindow.KafkaBrokerListProvider
import kafkaplugin.toolwindow.KafkaBrokerListener
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class KafkaBrokerManager(val myProject: Project) : ProjectComponent {

    companion object Instance {
        fun getInstance(project: Project): KafkaBrokerManager {
            return project.getComponent(KafkaBrokerManager::class.java)
        }
    }

    private val myBrokers = MultiMap.createConcurrentSet<VirtualFile, KafkaBroker>()
    private val myBrokersRootOrder = ArrayList<String>()
    private var myProviders: MutableMap<String, KafkaBrokerListProvider> = mutableMapOf()
    private val myListeners = ContainerUtil.createLockFreeCopyOnWriteList<KafkaBrokerListener>()
    private val myName2BrokerRoots = TreeMap<String, List<TreeItem<Pair<AbstractUrl, String>>>>()

    @Synchronized
    fun createNewList(listName: String) {
        myName2BrokerRoots[listName] = ArrayList()
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

    fun getAvailableBrokerListNames(): ArrayList<String> {
        return ArrayList(myBrokersRootOrder)
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

    fun getBrokerListRootUrls(name: String): List<TreeItem<Pair<AbstractUrl, String>>> {
        val pairs = myName2BrokerRoots.get(name)
        return if (pairs == null) java.util.ArrayList() else pairs
    }

    internal fun createRootNodes(): List<AbstractTreeNode<String>> {
        val result = java.util.ArrayList<AbstractTreeNode<String>>()
        for (listName in myBrokersRootOrder) {
            result.add(KafkaBrokerListNode(myProject, listName))
        }
        val providers = ArrayList<KafkaBrokerListProvider>(getProviders().values)
        for (provider in providers) {
            result.add(provider.createBrokerListNode())
        }
        return result
    }

    private fun getProviders(): Map<String, KafkaBrokerListProvider> {
        return myProviders
    }

    fun getListProvider(name: String?): KafkaBrokerListProvider? {
        return getProviders()[name]
    }

    fun getVirtualFiles(listName: String, recursively: Boolean): Collection<VirtualFile> {
        if (getListProvider(listName) != null) return emptyList()
        val result = SmartList<VirtualFile>()
        val roots = myName2BrokerRoots[listName]
        if (!recursively) {
            for (item in roots!!) {
                val file = getVirtualFile(item)
                if (file != null) {
                    result.add(file)
                }
            }
        }
//        else {
//            iterateTreeItems(roots!!) { item ->
//                val file = getVirtualFile(item)
//                if (file != null) {
//                    result.add(file)
//                }
//            }
//        }
        return result
    }

    private fun getVirtualFile(item: TreeItem<Pair<AbstractUrl, String>>): VirtualFile? {
        val data = item.data
        val path = data.first.createPath(myProject)
        if (path != null && path.size == 1) {
            if (path[0] is PsiFile) {
                val virtualFile = (path[0] as PsiFile).virtualFile
                if (virtualFile != null && !virtualFile.isDirectory) {
                    return virtualFile
                }
            }
            if (path[0] is File) {
                val virtualFile = VfsUtil.findFileByIoFile(path[0] as File, false)
                if (virtualFile != null && !virtualFile.isDirectory) {
                    return virtualFile
                }
            }
        }
        return null
    }

    private fun iterateTreeItems(coll: Collection<TreeItem<Pair<AbstractUrl, String>>>,
                                 consumer: Consumer<TreeItem<Pair<AbstractUrl, String>>>) {
        val queue = ArrayDeque(coll)
        while (!queue.isEmpty()) {
            val item = queue.removeFirst()
            consumer.consume(item)
            val children = item.children
            if (children != null && !children.isEmpty()) {
                queue.addAll(children)
            }
        }
    }
}
