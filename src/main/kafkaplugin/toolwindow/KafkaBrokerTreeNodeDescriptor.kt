package kafkaplugin.toolwindow

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.ide.util.treeView.PresentableNodeDescriptor
import com.intellij.openapi.project.Project

class KafkaBrokerTreeNodeDescriptor(project: Project,
                                    val myElement: AbstractTreeNode<String>,
                                    parentDescriptor: NodeDescriptor<String>) : PresentableNodeDescriptor<AbstractTreeNode<String>>(project, parentDescriptor) {

    companion object {
        val EMPTY_ARRAY = arrayOf<KafkaBrokerTreeNodeDescriptor>()
    }

    override fun getElement(): AbstractTreeNode<String> {
        return myElement
    }

    override fun update(presentation: PresentationData) {
        myElement.update()
        presentation.copyFrom(myElement.getPresentation())
    }

    fun getBrokerRoot(): KafkaBrokerTreeNodeDescriptor? {
        var descriptor: KafkaBrokerTreeNodeDescriptor? = this
        while (descriptor != null && descriptor.parentDescriptor is KafkaBrokerTreeNodeDescriptor) {
            val parent = descriptor.parentDescriptor as KafkaBrokerTreeNodeDescriptor?
            if (parent != null && parent.parentDescriptor == null) {
                return descriptor
            }
            descriptor = parent
        }
        return descriptor
    }
}
