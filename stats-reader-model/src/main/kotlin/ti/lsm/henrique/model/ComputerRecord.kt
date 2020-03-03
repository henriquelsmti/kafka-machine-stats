package ti.lsm.henrique.model

data class ComputerRecord(
        override val key: String,
        val vendor: String,
        val processorSerialNumber: String,
        val baseboard: String,
        val processorIdentifier: String,
        val processors: String
) : KafkaRecord(key, "computer-record")