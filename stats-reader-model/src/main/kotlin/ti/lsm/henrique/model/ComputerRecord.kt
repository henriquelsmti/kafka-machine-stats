package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class ComputerRecord(
        override val key: String,
        val vendor: String,
        val processorSerialNumber: String,
        val baseboard: String,
        val processorIdentifier: String,
        val processors: String
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "computer-record"
    }
}