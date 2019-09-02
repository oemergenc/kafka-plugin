package kafkaplugin.utils

import com.intellij.CommonBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.util.*

class KafkaPluginBundle {
    companion object {
        var ourBundle: Reference<ResourceBundle>? = null

        @NonNls
        const val BUNDLE = "kafkaplugin.utils.KafkaPluginBundle"

        fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
            return CommonBundle.message(getBundle(), key, *params)
        }

        private fun getBundle(): ResourceBundle {
            var bundle: ResourceBundle? = null
            if (ourBundle != null) bundle = ourBundle!!.get()
            if (bundle == null) {
                bundle = ResourceBundle.getBundle(BUNDLE)
                ourBundle = SoftReference(bundle)
            }
            return bundle!!
        }
    }

}
