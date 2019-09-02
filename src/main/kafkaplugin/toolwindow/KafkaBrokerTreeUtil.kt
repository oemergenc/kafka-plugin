package kafkaplugin.toolwindow

import com.intellij.ide.dnd.aware.DnDAwareTree
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import kafkaplugin.broker.KafkaBrokerManager
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode

class KafkaBrokerTreeUtil(myProject: Project, brokerName: String) {

    companion object {

        fun getSelectedNodeDescriptors(tree: DnDAwareTree): Array<KafkaBrokerTreeNodeDescriptor> {
            val path = tree.selectionPaths ?: return KafkaBrokerTreeNodeDescriptor.EMPTY_ARRAY
            val result = ArrayList<KafkaBrokerTreeNodeDescriptor>()
            for (treePath in path) {
                val lastPathNode = treePath.lastPathComponent as DefaultMutableTreeNode
                val userObject = lastPathNode.userObject
                if (userObject !is KafkaBrokerTreeNodeDescriptor) {
                    continue
                }
                result.add(userObject)
            }
            return result.toTypedArray()
        }

        fun getProvider(manager: KafkaBrokerManager, descriptor: KafkaBrokerTreeNodeDescriptor): KafkaBrokerListProvider? {
            var treeNode: AbstractTreeNode<*>? = descriptor.element
            while (treeNode != null && treeNode !is KafkaBrokerListNode) {
                treeNode = treeNode.parent
            }
            if (treeNode != null) {
                val name = (treeNode as KafkaBrokerListNode).value
                return manager.getListProvider(name)
            }
            return null
        }

    }
}
