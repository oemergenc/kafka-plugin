package kafkaplugin.toolwindow

import com.intellij.ide.util.treeView.AbstractTreeBuilder
import com.intellij.ide.util.treeView.AbstractTreeStructure
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl
import com.intellij.openapi.fileChooser.ex.RootFileElement
import com.intellij.openapi.fileChooser.impl.FileTreeBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kafkaplugin.Icons
import java.util.*
import javax.swing.Icon
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel

class PluginFileSystemTree(project: Project,
                           myTree: MyTree) :
        FileSystemTreeImpl(project,
                createFileChooserDescriptor(),
                myTree, null, null, null) {

    override fun createTreeBuilder(
            tree: JTree,
            treeModel: DefaultTreeModel,
            treeStructure: AbstractTreeStructure,
            comparator: Comparator<NodeDescriptor<*>>,
            descriptor: FileChooserDescriptor,
            onInitialized: Runnable?
    ): AbstractTreeBuilder {
        return object : FileTreeBuilder(tree, treeModel, treeStructure, comparator, descriptor, onInitialized) {
            override fun isAutoExpandNode(nodeDescriptor: NodeDescriptor<*>) = nodeDescriptor.element is RootFileElement
        }
    }

    companion object {

        private fun createFileChooserDescriptor(): FileChooserDescriptor {
            val descriptor = object : FileChooserDescriptor(true, true, true, false, true, true) {
                override fun getIcon(file: VirtualFile): Icon {
                    return Icons.pluginIcon
                }

                override fun getName(virtualFile: VirtualFile) = virtualFile.name
                override fun getComment(virtualFile: VirtualFile?) = ""
            }.also {
                it.withShowFileSystemRoots(false)
                it.withTreeRootVisible(false)
            }

//            ApplicationManager.getApplication().runWriteAction {
//                descriptor.setRoots(VfsUtil.createDirectoryIfMissing(LivePluginPaths.livePluginsPath))
//            }

            return descriptor
        }
    }
}