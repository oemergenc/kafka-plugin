package kafkaplugin.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.ExpandAllAction
import com.intellij.ide.dnd.aware.DnDAwareTree
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.treeStructure.actions.CollapseAllAction
import com.intellij.util.EditSourceOnDoubleClickHandler
import com.intellij.util.EditSourceOnEnterKeyHandler
import kafkaplugin.DeleteBrokerAction
import kafkaplugin.Icons
import kafkaplugin.broker.KafkaBrokerManager
import kafkaplugin.toolwindow.addplugin.AddNewBrokerAction
import kafkaplugin.toolwindow.settingsmenu.RunAllPluginsOnIDEStartAction
import java.awt.GridLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class KafkaBrokerTreeViewPanel(myProject: Project) : SimpleToolWindowPanel(true),
        DataProvider, Disposable {

    private val myKafkaBrokerManager: KafkaBrokerManager = KafkaBrokerManager.getInstance(myProject)

    var myTree: DnDAwareTree
    var myBrokerTreeStructure: KafkaBrokerTreeStructure = KafkaBrokerTreeStructure(myProject)
    var myBuilder: KafkaBrokerTreeViewBuilder

    init {
        val root = DefaultMutableTreeNode()
        root.userObject = myBrokerTreeStructure.rootElement
        val treeModel = DefaultTreeModel(root)
        myTree = DnDAwareTree(treeModel)
        myBuilder = KafkaBrokerTreeViewBuilder(myProject, myTree, treeModel, myBrokerTreeStructure)

        myKafkaBrokerManager.addBrokerListener(object : KafkaBrokerListener {
            override fun rootsChanged() {
                myBuilder.updateFromRoot()
                myTree.repaint()
            }

            override fun brokerAdded(listName: String) {
                myBuilder.updateFromRoot()
                myTree.repaint()
            }

            override fun brokerRemoved(listName: String) {
                myBuilder.updateFromRoot()
                myTree.repaint()
            }
        }, this@KafkaBrokerTreeViewPanel)
    }

    override fun getData(dataId: String): Any? {
        println(dataId)
        return null
    }

    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setupToolWindow(window: ToolWindowEx) {
        val collapseAction = CollapseAllAction(myTree)
        EditSourceOnDoubleClickHandler.install(myTree)
        EditSourceOnEnterKeyHandler.install(myTree)
        collapseAction.templatePresentation.setIcon(AllIcons.Actions.Collapseall)
        collapseAction.templatePresentation.setHoveredIcon(AllIcons.Actions.Collapseall)
        window.setTitleActions(collapseAction)
        window.icon = Icons.kafkaToolWindowIcon

        val group = DefaultActionGroup()
        window.setAdditionalGearActions(group)

        toolbar = createToolBar()
    }

    private fun createToolBar(): JComponent {
        fun AnAction.withIcon(icon: Icon) = apply { templatePresentation.icon = icon }

        val actionGroup = DefaultActionGroup().also {
            it.add(createAddBrokerGroup().withIcon(Icons.addPluginIcon))
            it.add(DeleteBrokerAction())
            it.addSeparator()
            it.add(ExpandAllAction().withIcon(Icons.expandAllIcon))
            it.add(com.intellij.ide.actions.CollapseAllAction().withIcon(Icons.collapseAllIcon))
            it.addSeparator()
            it.add(createSettingsGroup().withIcon(Icons.settingsIcon))
        }

        return JPanel(GridLayout()).also {
            // this is a "hack" to force drop-down box appear below button
            // (see com.intellij.openapi.actionSystem.ActionPlaces#isToolbarPlace implementation for details)
            val place = ActionPlaces.EDITOR_TOOLBAR
            it.add(ActionManager.getInstance().createActionToolbar(place, actionGroup, true).component)
        }
    }

    private fun createAddBrokerGroup() =
            DefaultActionGroup("Add Broker", true).also {
                it.add(AddNewBrokerAction())
            }

    private fun createSettingsGroup() =
            object : DefaultActionGroup("Settings", true) {
                // Without this IntelliJ calls update() on first action in the group even if the action group is collapsed
                override fun disableIfNoVisibleChildren() = false
            }.also {
                it.add(RunAllPluginsOnIDEStartAction())
            }
}