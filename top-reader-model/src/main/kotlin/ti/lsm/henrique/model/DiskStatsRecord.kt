package ti.lsm.henrique.model


data class DiskStatsRecord(
        override val key: String,
        val totalRead: Double,
        val totalWrite: Double
) : KafkaRecord(key, "disk-stats-record")