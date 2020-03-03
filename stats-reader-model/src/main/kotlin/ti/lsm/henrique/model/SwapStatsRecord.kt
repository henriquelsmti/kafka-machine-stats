package ti.lsm.henrique.model

data class SwapStatsRecord(override val key: String,
                           val total:Double,
                           val free:Double,
                           val used:Double,
                           val availMem:Double
) : KafkaRecord(key, "cpu-stats-record")
