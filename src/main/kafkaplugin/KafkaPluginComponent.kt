package kafkaplugin

import com.intellij.notification.NotificationGroup
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import kafkaplugin.toolwindow.PluginToolWindowManager


class KafkaPluginAppComponent : DumbAware {
    init {
        PluginToolWindowManager().init()
    }

    companion object {
        const val kafkaPluginId = "KafkaPlugin"
        private val logger = Logger.getInstance(KafkaPluginAppComponent::class.java)
        private val kafkaPluginNotificationGroup = NotificationGroup.balloonGroup("Kafka Plugin")

        private const val defaultIdeaOutputFolder = "out"
    }
}
