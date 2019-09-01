package kafkaplugin.toolwindow

import com.intellij.ide.dnd.aware.DnDAwareTree
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer

class KafkaBrokerPanel(myProject: Project) {

    private var myTree: DnDAwareTree
    var myViewPanel: KafkaBrokerTreeViewPanel = KafkaBrokerTreeViewPanel(myProject)
    private var myTreeStructure: KafkaBrokerTreeStructure
    private var myTreeBuilder: KafkaBrokerTreeViewBuilder

    init {
        myTree = myViewPanel.myTree
        myTreeBuilder = myViewPanel.myBuilder
        Disposer.register(myProject, myTreeBuilder)
        myTreeStructure = myViewPanel.myBrokerTreeStructure
    }
}