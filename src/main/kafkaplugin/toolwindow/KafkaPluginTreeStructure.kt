package kafkaplugin.toolwindow

import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTreeStructure

class KafkaPluginTreeStructure(private val rootNode: SimpleNode) : SimpleTreeStructure() {

    override fun getRootElement(): SimpleNode {
        return rootNode
    }
}
