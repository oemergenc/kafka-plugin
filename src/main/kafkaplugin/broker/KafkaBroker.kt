package kafkaplugin.broker

import com.intellij.openapi.vfs.VirtualFile

class KafkaBroker(val name: String,
                  val bootstrapUrl: String,
                  val file: VirtualFile) {
}
