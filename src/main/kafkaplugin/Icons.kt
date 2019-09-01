package kafkaplugin

import com.intellij.icons.AllIcons.*
import com.intellij.openapi.util.IconLoader
import com.intellij.util.IconUtil.toSize
import javax.swing.UIManager

object Icons {
    val pluginIcon = Nodes.Plugin!!
    val addPluginIcon = General.Add!!
    val newPluginIcon = pluginIcon
    val deletePluginIcon = General.Remove!!
    val refreshPluginsPanelIcon = Actions.Refresh!!
    val pluginToolwindowIcon = toSize(
            IconLoader.getIcon(if (isUnderDarcula()) "/kafkaplugin/plugin_dark.svg" else "/kafkaplugin/plugin.svg"),
            13, 13 // resize because IJ logs warning if toolwindow icon is not 13x13
    )
    val kafkaToolWindowIcon = toSize(IconLoader.getIcon("/kafkaplugin/kafka_icon_12x12.svg"), 13, 13)
    val runPluginIcon = Actions.Execute!!
    val testPluginIcon = RunConfigurations.Junit!!
    val expandAllIcon = Actions.Expandall!!
    val collapseAllIcon = Actions.Collapseall!!
    val settingsIcon = General.GearPlain!!
    val helpIcon = Actions.Help!!
    val newFolderIcon = Nodes.Folder!!
}

// Copied from UIUtil to keep plugin working while the function is being moved to StartupUiUtil
private fun isUnderDarcula() = UIManager.getLookAndFeel().name.contains("Darcula")