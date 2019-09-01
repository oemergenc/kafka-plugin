package kafkaplugin.toolwindow

interface KafkaBrokerListener {

    fun brokerAdded(b: String) {}
    fun brokerRemoved(b: String) {}
    fun rootsChanged()
}