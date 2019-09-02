package kafkaplugin.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.ide.favoritesTreeView.ProjectViewNodeWithChildrenList
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.impl.AbstractUrl
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.util.TreeItem
import kafkaplugin.broker.KafkaBrokerManager
import java.util.*

class KafkaBrokerListNode(myProject: Project, brokerName: String)
    : AbstractTreeNode<String>(myProject, brokerName) {

    companion object {
        fun getBrokerRoots(project: Project, listName: String, listNode: KafkaBrokerListNode): Collection<AbstractTreeNode<*>> {
            val pairs = KafkaBrokerManager.getInstance(project).getBrokerListRootUrls(listName)
            return if (pairs.isEmpty()) emptyList() else createBrokerRoots(project, pairs, listNode)
        }

        private fun createBrokerRoots(project: Project,
                                      urls: Collection<TreeItem<Pair<AbstractUrl, String>>>,
                                      me: AbstractTreeNode<*>): Collection<AbstractTreeNode<*>> {
            val result = ArrayList<AbstractTreeNode<*>>()
            processUrls(project, urls, result, me)
            return result
        }

        private fun processUrls(project: Project,
                                urls: Collection<TreeItem<Pair<AbstractUrl, String>>>,
                                result: MutableCollection<in AbstractTreeNode<*>>, me: AbstractTreeNode<*>) {
            for (pair in urls) {
                val abstractUrl = pair.data.getFirst()
                val path = abstractUrl.createPath(project)
                if (path == null || path.size < 1 || path[0] == null) {
                    continue
                }
                try {
                    val node = ProjectViewNode.createTreeNode(KafkaBrokerListNode::class.java, project, path[path.size - 1], null)
                    node.parent = me
                    node.index = result.size
                    result.add(node)

                    if (node is ProjectViewNodeWithChildrenList<*>) {
                        val children = pair.children
                        if (children != null && !children.isEmpty()) {
                            val childList = ArrayList<AbstractTreeNode<*>>()
                            processUrls(project, children, childList, node)
                            for (treeNode in childList) {
                                node.addChild(treeNode)
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Error in KafkaBrokerListNode")
                }

            }
        }
    }

    fun getProvider(): KafkaBrokerListProvider? {
        return null
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        return getBrokerRoots(myProject, myName, this)
    }

    override fun update(presentation: PresentationData) {
        presentation.setIcon(AllIcons.Toolwindows.ToolWindowFavorites)
        presentation.presentableText = myName
    }

    fun getBrokerRoots(project: Project, brokerName: String, listNode: KafkaBrokerListNode): Collection<AbstractTreeNode<*>> {
        val pairs = KafkaBrokerManager.getInstance(project).getBrokerListRootUrls(brokerName)
        return if (pairs.isEmpty()) emptyList() else createBrokerRoots(project, pairs, listNode)
    }

    private fun createBrokerRoots(project: Project,
                                  urls: Collection<TreeItem<Pair<AbstractUrl, String>>>,
                                  me: AbstractTreeNode<*>): Collection<AbstractTreeNode<*>> {
        val result = ArrayList<AbstractTreeNode<*>>()
        processUrls(project, urls, result, me)
        return result
    }

}
