/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kafkaplugin.toolwindow

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import kafkaplugin.broker.KafkaBrokerManager
import java.util.*

class KafkaBrokerRootNode(project: Project) : AbstractTreeNode<String>(project, "") {
    private var myFavoritesRoots: List<AbstractTreeNode<String>>? = null

    override fun getChildren(): Collection<AbstractTreeNode<String>> {
        if (myFavoritesRoots == null) {
            myFavoritesRoots = ArrayList(KafkaBrokerManager.getInstance(myProject).createRootNodes())
        }
        return myFavoritesRoots as List<AbstractTreeNode<String>>
    }

    fun rootsChanged() {
        myFavoritesRoots = null
    }

    public override fun update(presentation: PresentationData) {}
}
