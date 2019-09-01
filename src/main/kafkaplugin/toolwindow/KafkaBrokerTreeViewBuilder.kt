package kafkaplugin.toolwindow

import com.intellij.ide.projectView.BaseProjectTreeBuilder
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase
import com.intellij.openapi.project.Project
import kafkaplugin.broker.KafkaBrokerManager
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel

class KafkaBrokerTreeViewBuilder(myProject: Project,
                                 myTree: JTree,
                                 treeModel: DefaultTreeModel,
                                 treeStructure: ProjectAbstractTreeStructureBase
) : BaseProjectTreeBuilder(myProject, myTree, treeModel, treeStructure, null) {

    init {

        val brokerListener = object : KafkaBrokerListener {
            override fun rootsChanged() {
                updateFromRoot()
            }

            override fun brokerAdded(listName: String) {
                updateFromRoot()
            }

            override fun brokerRemoved(listName: String) {
                updateFromRoot()
            }
        }
        initRootNode()
        KafkaBrokerManager.getInstance(myProject).addBrokerListener(brokerListener, this)
    }
}