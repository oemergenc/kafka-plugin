package kafkaplugin.toolwindow

import com.intellij.ide.DeleteProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileChooser.actions.VirtualFileDeleteProvider
import com.intellij.openapi.fileChooser.ex.FileNodeDescriptor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.tree.TreeUtil
import org.jetbrains.annotations.NonNls

class MyTree constructor(private val project: Project) : Tree(), DataProvider {
    private val deleteProvider = FileDeleteProviderWithRefresh()

    init {
        emptyText.text = "No plugins to show"
        isRootVisible = false
    }

    override fun getData(@NonNls dataId: String): Any? =
            when (dataId) {
                PlatformDataKeys.NAVIGATABLE_ARRAY.name -> // need this to be able to open files in toolwindow on double-click/enter
                    TreeUtil.collectSelectedObjectsOfType(this, FileNodeDescriptor::class.java)
                            .map { OpenFileDescriptor(project, it.element.file) }
                            .toTypedArray()
                PlatformDataKeys.DELETE_ELEMENT_PROVIDER.name -> deleteProvider
                else -> null
            }

    private class FileDeleteProviderWithRefresh : DeleteProvider {
        private val fileDeleteProvider = VirtualFileDeleteProvider()

        override fun deleteElement(dataContext: DataContext) {
            fileDeleteProvider.deleteElement(dataContext)
        }

        override fun canDeleteElement(dataContext: DataContext): Boolean {
            return fileDeleteProvider.canDeleteElement(dataContext)
        }
    }
}