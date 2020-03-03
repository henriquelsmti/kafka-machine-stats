package ti.lsm.henrique.model

data class LostRecord(override val key: String, val line: String) : KafkaRecord(key, "lost-record")