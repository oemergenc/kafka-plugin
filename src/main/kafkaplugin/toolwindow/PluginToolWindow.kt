package kafkaplugin.toolwindow

import com.intellij.ide.actions.CollapseAllAction
import com.intellij.ide.actions.ExpandAllAction
import com.intellij.ide.ui.customization.CustomizationUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileChooser.FileSystemTree
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Ref
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.util.EditSourceOnDoubleClickHandler
import com.intellij.util.EditSourceOnEnterKeyHandler
import kafkaplugin.DeleteBrokerAction
import kafkaplugin.Icons
import kafkaplugin.toolwindow.addplugin.AddNewBrokerAction
import kafkaplugin.toolwindow.popup.NewElementPopupAction
import kafkaplugin.toolwindow.settingsmenu.RunAllPluginsOnIDEStartAction
import java.awt.GridLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

class PluginToolWindow(val project: Project) {
    private val pluginsToolWindowId = "Kafka plugin"
    private var fsTreeRef = Ref<FileSystemTree>()
    private lateinit var panel: SimpleToolWindowPanel

    init {
        val toolWindowManager = ToolWindowManager.getInstance(project)
        toolWindowManager.registerToolWindow(pluginsToolWindowId, false, ToolWindowAnchor.RIGHT, project, true).also {
            it.icon = Icons.pluginToolwindowIcon
            it.contentManager.addContent(createContent(project))
        }

        PluginToolWindowManager.add(this)

        Disposer.register(project, Disposable {
            toolWindowManager.unregisterToolWindow(pluginsToolWindowId)
            PluginToolWindowManager.remove(this)
        })
    }

    private fun createContent(project: Project): Content {
        val fsTree = createFsTree(project)
        fsTreeRef = Ref.create(fsTree)
        fsTree.installPopupMenu()

        panel = MySimpleToolWindowPanel(true, fsTreeRef).also {
            it.add(ScrollPaneFactory.createScrollPane(fsTree.tree))
            it.toolbar = createToolBar()
        }
        return ContentFactory.SERVICE.getInstance().createContent(panel, "", false)
    }

    fun updateTree() {
        fsTreeRef.get().updateTree()
    }

    private fun createToolBar(): JComponent {
        fun AnAction.withIcon(icon: Icon) = apply { templatePresentation.icon = icon }

        val actionGroup = DefaultActionGroup().also {
            it.add(createAddPluginsGroup().withIcon(Icons.addPluginIcon))
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

    private fun createAddPluginsGroup() =
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


    companion object {

        private fun FileSystemTree.installPopupMenu() {
            fun shortcutsOf(actionId: String) = KeymapManager.getInstance().activeKeymap.getShortcuts(actionId)

            val action = NewElementPopupAction()
            action.registerCustomShortcutSet(CustomShortcutSet(*shortcutsOf("NewElement")), tree)

            CustomizationUtil.installPopupHandler(tree, "LivePlugin.Popup", ActionPlaces.UNKNOWN)
        }

        private fun createFsTree(project: Project): FileSystemTree {
            val myTree = MyTree(project)
            EditSourceOnDoubleClickHandler.install(myTree)
            EditSourceOnEnterKeyHandler.install(myTree)
            return PluginFileSystemTree(project, myTree)
        }
    }
}