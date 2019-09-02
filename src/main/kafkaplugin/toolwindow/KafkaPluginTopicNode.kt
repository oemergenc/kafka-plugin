package kafkaplugin.toolwindow

import com.intellij.ui.treeStructure.SimpleNode

class KafkaPluginTopicNode : SimpleNode() {

    override fun toString(): String {
        return "A topic entry"
    }

    override fun getChildren(): Array<SimpleNode> {
        return arrayOf()
    }

    override fun isAlwaysLeaf(): Boolean {
        return true
    }
}
