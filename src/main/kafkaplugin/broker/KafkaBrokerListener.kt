package kafkaplugin.broker

import com.intellij.util.messages.Topic

interface KafkaBrokerListener {

    companion object {
        val TOPIC: Topic<KafkaBrokerListener> = Topic.create("KafkaBrokers", KafkaBrokerListener::class.java)
    }

    fun brokerAdded(b: KafkaBroker) {}
    fun brokerRemoved(b: KafkaBroker) {}
    fun brokerChanged(b: KafkaBroker) {}
}