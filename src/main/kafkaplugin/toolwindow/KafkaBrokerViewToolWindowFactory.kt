package kafkaplugin.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx

class KafkaBrokerViewToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentManager = toolWindow.contentManager
        val panel = KafkaBrokerPanel(project).myViewPanel
        panel.setupToolWindow(toolWindow as ToolWindowEx)
        val content = contentManager.factory.createContent(panel, null, false)
        contentManager.addContent(content)
    }

    override fun isDoNotActivateOnStart(): Boolean {
        return true
    }
}
