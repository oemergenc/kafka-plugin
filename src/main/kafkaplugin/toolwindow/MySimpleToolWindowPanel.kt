package kafkaplugin.toolwindow

import com.intellij.ide.DefaultTreeExpander
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileChooser.FileSystemTree
import com.intellij.openapi.fileChooser.ex.FileChooserKeys
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Ref
import org.jetbrains.annotations.NonNls

class MySimpleToolWindowPanel(vertical: Boolean, private val fileSystemTree: Ref<FileSystemTree>) : SimpleToolWindowPanel(vertical) {
    /**
     * Provides context for actions in plugin tree popup popup menu.
     * Without it the actions will be disabled or won't work.
     *
     * Implicitly used by
     * [com.intellij.openapi.fileChooser.actions.NewFileAction],
     * [com.intellij.openapi.fileChooser.actions.NewFolderAction],
     * [com.intellij.openapi.fileChooser.actions.FileDeleteAction]
     */
    override fun getData(@NonNls dataId: String): Any? =
            when (dataId) {
                FileSystemTree.DATA_KEY.name -> {
                    // This is used by "create directory/file" actions to get execution context
                    // (without it they will be disabled or won't work).
                    fileSystemTree.get()
                }
                FileChooserKeys.DELETE_ACTION_AVAILABLE.name -> true
                PlatformDataKeys.VIRTUAL_FILE_ARRAY.name -> fileSystemTree.get().selectedFiles
                PlatformDataKeys.TREE_EXPANDER.name -> DefaultTreeExpander(fileSystemTree.get().tree)
                else -> super.getData(dataId)
            }
}