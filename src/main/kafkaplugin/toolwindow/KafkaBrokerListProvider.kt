package kafkaplugin.toolwindow

import com.intellij.openapi.project.Project

class KafkaBrokerListProvider(myProject: Project,
                              brokerName: String) {
    private var myNode: KafkaBrokerListNode = KafkaBrokerListNode(myProject, brokerName)

    fun createBrokerListNode(): KafkaBrokerListNode {
        return myNode
    }
}