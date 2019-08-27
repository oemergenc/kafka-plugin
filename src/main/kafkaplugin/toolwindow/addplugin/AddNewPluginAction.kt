package kafkaplugin.toolwindow.addplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import kafkaplugin.Icons

class AddNewGroovyPluginAction : AddNewPluginAction(
        text = "Groovy Plugin",
        description = "Create new Groovy plugin"
)

open class AddNewPluginAction(
        text: String,
        description: String
) : AnAction(text, description, Icons.newPluginIcon), DumbAware {

    private val log = Logger.getInstance(AddNewPluginAction::class.java)
    private val addNewPluginTitle = "Add $text"

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        println("This is a message $project")
    }
}

