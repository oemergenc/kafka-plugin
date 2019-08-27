package kafkaplugin.toolwindow.popup

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.actions.NewFolderAction
import kafkaplugin.Icons
import javax.swing.Icon

class NewTextFileAction : NewFileAction("Text File", AllIcons.FileTypes.Text)

class NewDirectoryAction : NewFolderAction("Directory", "", Icons.newFolderIcon)

private val inferIconFromFileType: Icon? = null