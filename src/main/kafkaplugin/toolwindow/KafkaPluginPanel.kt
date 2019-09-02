package kafkaplugin.toolwindow

import com.intellij.ide.actions.CollapseAllAction
import com.intellij.ide.actions.ExpandAllAction
import com.intellij.ide.util.treeView.AbstractTreeBuilder
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.ui.treeStructure.SimpleTreeStructure
import kafkaplugin.DeleteBrokerAction
import kafkaplugin.Icons
import kafkaplugin.toolwindow.addplugin.AddNewBrokerAction
import kafkaplugin.toolwindow.settingsmenu.RunAllPluginsOnIDEStartAction
import java.awt.GridLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

class KafkaPluginPanel(val project: Project) : SimpleToolWindowPanel(true) {

    init {
        val reviewTreeStructure = createTreeStructure()
        val model = KafkaPluginTreeModel()
        model.insertNodeInto()
        val reviewTree = SimpleTree(model)

        AbstractTreeBuilder(reviewTree, model, reviewTreeStructure, null)
        reviewTree.invalidate()
        toolbar = createToolBar()
        val scrollPane = ScrollPaneFactory.createScrollPane(reviewTree)
        setContent(scrollPane)
    }

    private fun createTreeStructure(): SimpleTreeStructure {
        val rootNode = KafkaPluginBrokerNode()
        return KafkaPluginTreeStructure(rootNode)
    }

    private fun createToolBar(): JComponent {
        fun AnAction.withIcon(icon: Icon) = apply { templatePresentation.icon = icon }

        val actionGroup = DefaultActionGroup().also {
            it.add(createAddBrokerGroup().withIcon(Icons.addPluginIcon))
            it.add(DeleteBrokerAction())
            it.addSeparator()
            it.add(ExpandAllAction().withIcon(Icons.expandAllIcon))
            it.add(CollapseAllAction().withIcon(Icons.collapseAllIcon))
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
            DefaultActionGroup("Add a new Broker", true).also {
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
