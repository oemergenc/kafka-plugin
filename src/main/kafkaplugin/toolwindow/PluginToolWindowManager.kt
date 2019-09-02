package kafkaplugin.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import java.util.*

class PluginToolWindowManager {
    fun init() {
        val connection = ApplicationManager.getApplication().messageBus.connect()
        connection.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
            override fun projectOpened(project: Project) {
                PluginToolWindow(project)
            }
        })
    }

    companion object {
        private val toolWindows = HashSet<PluginToolWindow>()

        fun add(pluginToolWindow: PluginToolWindow) {
            toolWindows.add(pluginToolWindow)
        }

        fun remove(pluginToolWindow: PluginToolWindow) {
            toolWindows.remove(pluginToolWindow)
        }

        fun reloadPluginTreesInAllProjects() {
            toolWindows.forEach { it.updateTree() }
        }
    }
}


