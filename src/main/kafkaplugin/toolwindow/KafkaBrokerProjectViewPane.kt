package kafkaplugin.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.SelectInTarget
import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.AbstractProjectViewPane
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ActionCallback
import com.intellij.openapi.vfs.VirtualFile
import kafkaplugin.broker.KafkaBrokerManager
import kafkaplugin.broker.KafkaBrokerTopicManager
import javax.swing.Icon
import javax.swing.JComponent

class KafkaBrokerProjectViewPane(myProject: Project,
                                 val myBrokerManager: KafkaBrokerManager) : AbstractProjectViewPane(myProject) {

    companion object {
        const val ID = "Kafka Plugin"
    }

    private lateinit var myViewPanel: KafkaBrokerTreeViewPanel

    init {
        val brokerListener = object : KafkaBrokerListener {
            private var enabled = true

            override fun rootsChanged() {}

            override fun brokerAdded(listName: String) {
                refreshMySubIdsAndSelect(listName)
            }

            override fun brokerRemoved(listName: String) {
                val selectedSubId = subId
                refreshMySubIdsAndSelect(selectedSubId)
            }

            private fun refreshMySubIdsAndSelect(listName: String?) {
                var listName = listName
                if (!enabled) {
                    return
                }

                try {
                    enabled = false
                    val projectView = ProjectView.getInstance(myProject)
                    projectView.removeProjectPane(this@KafkaBrokerProjectViewPane)
                    projectView.addProjectPane(this@KafkaBrokerProjectViewPane)
                    if (!myBrokerManager.getAvailableBrokerListNames().contains(listName)) {
                        listName = null
                    }
                    projectView.changeView(ID, listName)
                } finally {
                    enabled = true
                }
            }
        }
        myBrokerManager.addBrokerListener(brokerListener, this)
    }

    override fun createComponent(): JComponent {
        myViewPanel = KafkaBrokerTreeViewPanel(myProject)
        myTree = myViewPanel.myTree
        treeBuilder = myViewPanel.myBuilder
        myTreeStructure = myViewPanel.myBrokerTreeStructure
        installComparator()
        enableDnD()
        return myViewPanel
    }

    override fun getId(): String {
        return ID
    }

    override fun getTitle(): String {
        return ID
    }

    override fun createSelectInTarget(): SelectInTarget {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIcon(): Icon {
        return AllIcons.Toolwindows.ToolWindowFavorites
    }

    override fun select(element: Any?, file: VirtualFile?, requestFocus: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateFromRoot(restoreExpandedPaths: Boolean): ActionCallback {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}