package kafkaplugin.toolwindow

import com.intellij.ide.favoritesTreeView.FavoriteNodeProvider
import com.intellij.ide.favoritesTreeView.FavoritesTreeNodeDescriptor
import com.intellij.ide.projectView.SettingsProvider
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.ProjectTreeStructure
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.util.ArrayUtil
import com.intellij.util.ArrayUtilRt
import java.util.*

class KafkaBrokerTreeStructure(myProject: Project) : ProjectTreeStructure(myProject, KafkaBrokerProjectViewPane.ID) {

    val myNonProjectProvider = MyProvider(myProject)

    override fun createRoot(project: Project, settings: ViewSettings): AbstractTreeNode<*> {
        return KafkaBrokerRootNode(project)
    }

    fun rootsChanged() {
        (rootElement as KafkaBrokerRootNode).rootsChanged()
    }

    override fun getChildElements(element: Any): Array<Any> {
        if (element !is AbstractTreeNode<*>) {
            return ArrayUtilRt.EMPTY_OBJECT_ARRAY
        }

        try {
            if (element !is KafkaBrokerListNode) {
                val elements = super.getChildElements(element)
                if (elements.size > 0) return elements

                val settings = if (element is SettingsProvider) (element as SettingsProvider).settings else ViewSettings.DEFAULT
                return ArrayUtil.toObjectArray(myNonProjectProvider.modify(element, ArrayList(), settings))
            }

            val result = ArrayList<AbstractTreeNode<*>>()
            if (element.getProvider() != null) {
                return ArrayUtil.toObjectArray(element.children)
            }
            if (element.getProvider() != null) {
                return ArrayUtil.toObjectArray(element.children)
            }
            val roots = KafkaBrokerListNode.getBrokerRoots(myProject, element.name!!, element)
            for (abstractTreeNode in roots) {
                val value = abstractTreeNode.value ?: continue

                if (value is PsiElement && !value.isValid) continue
                if (value is SmartPsiElementPointer<*> && value.element == null) continue

                var invalid = false
                for (nodeProvider in FavoriteNodeProvider.EP_NAME.getExtensions(myProject)) {
                    if (nodeProvider.isInvalidElement(value)) {
                        invalid = true
                        break
                    }
                }
                if (invalid) continue

                result.add(abstractTreeNode)
            }
            return ArrayUtil.toObjectArray(result)
        } catch (e: Exception) {
            println("Your mother")
            println(e)
//            LOGGER.error(e)
        }

        return ArrayUtilRt.EMPTY_OBJECT_ARRAY
    }

    override fun getParentElement(element: Any): Any? {
        var parent: AbstractTreeNode<*>? = null
        if (element === rootElement) {
            return null
        }
        if (element is AbstractTreeNode<*>) {
            parent = element.parent
        }
        return parent ?: rootElement
    }

    override fun createDescriptor(element: Any, parentDescriptor: NodeDescriptor<*>?): NodeDescriptor<*> {
        return FavoritesTreeNodeDescriptor(myProject, parentDescriptor, element as AbstractTreeNode<*>)
    }

    class MyProvider internal constructor(private val myProject: Project) : TreeStructureProvider {

        override fun modify(parent: AbstractTreeNode<*>,
                            children: Collection<AbstractTreeNode<*>>,
                            settings: ViewSettings): Collection<AbstractTreeNode<*>> {
            if (parent is PsiDirectoryNode && children.isEmpty()) {
                val virtualFile = parent.virtualFile ?: return children
                val virtualFiles = virtualFile.children
                val result = ArrayList<AbstractTreeNode<*>>()
                val psiManager = PsiManager.getInstance(myProject) as PsiManagerImpl
                for (file in virtualFiles) {
                    val child: AbstractTreeNode<*>
                    if (file.isDirectory) {
                        val directory = psiManager.findDirectory(file) ?: continue
                        child = PsiDirectoryNode(myProject, directory, settings)
                    } else {
                        val psiFile = psiManager.findFile(file) ?: continue
                        child = PsiFileNode(myProject, psiFile, settings)
                    }
                    child.parent = parent
                    result.add(child)
                }
                return result
            }
            return children
        }
    }
}
