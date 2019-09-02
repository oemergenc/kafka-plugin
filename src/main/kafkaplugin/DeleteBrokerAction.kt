package kafkaplugin

import com.intellij.CommonBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationBundle
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile


internal class DeleteBrokerAction : AnAction("Delete Plugin", "Delete Plugin", Icons.deletePluginIcon), DumbAware {

    override fun actionPerformed(event: AnActionEvent) {
        println("DeleteAction")
    }

    override fun update(event: AnActionEvent) {
        println("DeleteAction Update")
    }

    companion object {
        private val logger = Logger.getInstance(DeleteBrokerAction::class.java)

        private fun userDoesNotWantToRemovePlugins(pluginRoots: Collection<VirtualFile>, project: Project?): Boolean {
            val pluginIds = pluginRoots.map { it.name }

            val message = when {
                pluginIds.size == 1 -> "Do you want to delete plugin \"" + pluginIds[0] + "\"?"
                pluginIds.size == 2 -> "Do you want to delete plugin \"" + pluginIds[0] + "\" and \"" + pluginIds[1] + "\"?"
                else -> "Do you want to delete plugins \"" + StringUtil.join(pluginIds, ", ") + "\"?"
            }
            val answer = Messages.showOkCancelDialog(
                    project,
                    message,
                    "Delete",
                    ApplicationBundle.message("button.delete"),
                    CommonBundle.getCancelButtonText(),
                    Messages.getQuestionIcon()
            )
            return answer != Messages.OK
        }
    }
}
