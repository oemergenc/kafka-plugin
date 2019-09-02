package kafkaplugin.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kafkaplugin.Icons

class KafkaPluginToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val pluginPanel = KafkaPluginPanel(project)
        val content = ContentFactory.SERVICE.getInstance().createContent(pluginPanel, "Kafka Plugin A", false)
        content.isCloseable = false
        toolWindow.icon = Icons.pluginToolwindowIcon
        contentManager.addContent(content)
    }
}