package kafkaplugin.broker

import com.intellij.util.messages.Topic

interface KafkaTopicListener {

    companion object {
        val TOPIC: Topic<KafkaTopicListener> = Topic.create("KafkaTopics", KafkaTopicListener::class.java)
    }

    fun topicAdded(b: KafkaBroker) {}
    fun topicRemoved(b: KafkaBroker) {}
    fun topicChanged(b: KafkaBroker) {}
}