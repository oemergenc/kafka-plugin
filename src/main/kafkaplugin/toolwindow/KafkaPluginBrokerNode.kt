package kafkaplugin.toolwindow

import com.intellij.ui.treeStructure.SimpleNode
import java.util.*

class KafkaPluginBrokerNode : SimpleNode() {

    private val myChildren = ArrayList<SimpleNode>()

    init {
        addChildren()
    }

    override fun getChildren(): Array<SimpleNode> {
        return myChildren.toTypedArray()
    }

    private fun addChildren() {
        for (x in 0..10)
            myChildren.add(KafkaPluginTopicNode())
    }

    override fun toString(): String {
        return "A Broker Node"
    }
}
