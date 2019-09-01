package kafkaplugin.broker

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAwareRunnable
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.MultiMap
import kafkaplugin.broker.KafkaBrokerListener.Companion.TOPIC
import org.jdom.Element


@State(name = "BookmarkManager", storages = [Storage(value = StoragePathMacros.WORKSPACE_FILE, deprecated = true)])
class KafkaBrokerManager(val myProject: Project) : ProjectComponent, PersistentStateComponent<Element> {
    private val myBrokers = MultiMap.createConcurrentSet<VirtualFile, KafkaBroker>()

    fun getInstance(project: Project): KafkaBrokerManager {
        return ServiceManager.getService(project, KafkaBrokerManager::class.java)
    }

    private fun getPublisher(): KafkaBrokerListener {
        return myProject.messageBus.syncPublisher(TOPIC)
    }

    fun addBroker(name: String, boostrapServerUrl: String, file: VirtualFile): KafkaBroker {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val broker = KafkaBroker(name, boostrapServerUrl, file)
        myBrokers.putValue(file, broker)
        getPublisher().brokerAdded(broker)
        return broker
    }

    fun removeBroker(broker: KafkaBroker) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val file = broker.file
        if (myBrokers.remove(file, broker)) {
            getPublisher().brokerRemoved(broker)
        }
    }

    fun getBrokers(): List<KafkaBroker> {
        return ContainerUtil.collect(myBrokers.values().iterator())
    }

    override fun getState(): Element? {
        val container = Element("KafkaBrokerManager")
        writeExternal(container)
        return container
    }

    private fun writeExternal(element: Element) {
        val brokers = ArrayList<KafkaBroker>(myBrokers.values())

        for (broker in brokers) {
            val bookmarkElement = Element("broker")
            bookmarkElement.setAttribute("url", broker.file.url)
            bookmarkElement.setAttribute("name", broker.name)
            bookmarkElement.setAttribute("bootstrapUrl", broker.bootstrapUrl)
            element.addContent(bookmarkElement)
        }
    }

    private fun readExternal(element: Element) {
        for (bookmarkElement in element.getChildren("broker")) {
            val url = bookmarkElement.getAttributeValue("url")
            val name = bookmarkElement.getAttributeValue("name")
            val bootstrapUrl = bookmarkElement.getAttributeValue("bootstrapUrl")
            val virtualFile = VirtualFileManager.getInstance().findFileByUrl(url)
            addBroker(name, bootstrapUrl, virtualFile!!)
        }
    }

    override fun loadState(state: Element) {
        StartupManager.getInstance(myProject).runWhenProjectIsInitialized({
            myBrokers.clear()
            readExternal(state)
        } as DumbAwareRunnable)
    }
}
