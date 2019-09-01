package kafkaplugin

import com.intellij.notification.NotificationGroup
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.io.FileUtilRt
import kafkaplugin.toolwindow.PluginToolWindowManager


object KafkaPluginPaths {
    val livePluginPath = FileUtilRt.toSystemIndependentName(PathManager.getPluginsPath() + "/KafkaPlugin/")
    val livePluginLibPath = FileUtilRt.toSystemIndependentName(PathManager.getPluginsPath() + "/KafkaPlugin/lib/")
    @JvmField
    val kafkaPluginPath = FileUtilRt.toSystemIndependentName(PathManager.getPluginsPath() + "/kafka-plugins")
}

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
