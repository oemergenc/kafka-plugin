package kafkaplugin.toolwindow

import com.intellij.ProjectTopics
import com.intellij.ide.projectView.BaseProjectTreeBuilder
import com.intellij.ide.projectView.ProjectViewPsiTreeChangeListener
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.AbstractTreeUpdater
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener
import com.intellij.openapi.util.ActionCallback
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import kafkaplugin.broker.KafkaBrokerManager
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class KafkaBrokerTreeViewBuilder(myProject: Project,
                                 myTree: JTree,
                                 treeModel: DefaultTreeModel,
                                 treeStructure: ProjectAbstractTreeStructureBase
) : BaseProjectTreeBuilder(myProject, myTree, treeModel, treeStructure, null) {

    init {

        val bus = myProject.messageBus.connect(this)
        val psiTreeChangeListener = object : ProjectViewPsiTreeChangeListener(myProject) {
            override fun getRootNode(): DefaultMutableTreeNode? {
                return this@KafkaBrokerTreeViewBuilder.rootNode
            }

            override fun getUpdater(): AbstractTreeUpdater? {
                return this@KafkaBrokerTreeViewBuilder.getUpdater()
            }

            override fun isFlattenPackages(): Boolean {
                return getStructure().isFlattenPackages
            }

            override fun childrenChanged(parent: PsiElement?, stopProcessingForThisModificationCount: Boolean) {
                val containingFile = if (parent is PsiDirectory) parent else parent!!.containingFile
                if (containingFile != null && findNodeByElement(containingFile) == null) {
                    queueUpdate(true)
                } else {
                    super.childrenChanged(parent, true)
                }
            }
        }
        bus.subscribe(ProjectTopics.PROJECT_ROOTS, object : ModuleRootListener {
            override fun rootsChanged(event: ModuleRootEvent) {
                queueUpdate(true)
            }
        })
        PsiManager.getInstance(myProject).addPsiTreeChangeListener(psiTreeChangeListener, this)
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

    fun getStructure(): KafkaBrokerTreeStructure {
        val structure = treeStructure
        assert(structure is KafkaBrokerTreeStructure)
        return structure as KafkaBrokerTreeStructure
    }

    fun getRoot(): AbstractTreeNode<String> {
        val rootElement = rootElement
        assert(rootElement is AbstractTreeNode<*>)
        return rootElement as AbstractTreeNode<String>
    }

    override fun updateFromRoot() {
        updateFromRootCB()
    }

    fun updateFromRootCB(): ActionCallback {
        getStructure().rootsChanged()
        if (isDisposed) return ActionCallback.DONE
        updater!!.cancelAllRequests()
        return queueUpdate()
    }
}