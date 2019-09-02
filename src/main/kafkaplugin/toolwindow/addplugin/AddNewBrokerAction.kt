package kafkaplugin.toolwindow.addplugin

import com.intellij.ide.IdeBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import kafkaplugin.Icons
import kafkaplugin.broker.KafkaBrokerManager

open class AddNewBrokerAction : AnAction("New broker", "Add a new Broker Connection", Icons.newPluginIcon), DumbAware {

    private val log = Logger.getInstance(AddNewBrokerAction::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        if (project != null) {
            doAddNewBroker(project)
        }
    }

    fun doAddNewBroker(project: Project): String? {
        val brokerManager = KafkaBrokerManager.getInstance(project)
        val name = Messages.showInputDialog(project,
                "Please enter the broker bootstrap address",
                "Add a new broker connection",
                Messages.getInformationIcon(),
                getUniqueName(project), object : InputValidator {
            override fun checkInput(inputString: String?): Boolean {
                return inputString != null && inputString.trim { it <= ' ' }.length > 0
            }

            override fun canClose(inputString: String): Boolean {
                var inputString = inputString
                inputString = inputString.trim { it <= ' ' }
                if (brokerManager.getAvailableBrokerListNames().contains(inputString)) {
                    Messages.showErrorDialog(project, "There was a problem while creating the broker", "Broker could not be added")
                    return false
                }
                return inputString.length > 0
            }
        })
        if (name == null || name.length == 0) return null
        brokerManager.createNewList(name)
        return name
    }

    private fun getUniqueName(project: Project): String {
        val names = KafkaBrokerManager.getInstance(project).getAvailableBrokerListNames()
        var i = 0
        while (true) {
            val newName = IdeBundle.message("favorites.list.unnamed", if (i > 0) i else "")
            if (names.contains(newName)) {
                i++
                continue
            }
            return newName
            i++
        }
    }
}
