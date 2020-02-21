package ti.lsm.henrique.model

data class CpuStatsRecord(override val key: String,
                          val us:Double,
                          val sy:Double,
                          val ni:Double,
                          val id:Double,
                          val wa:Double,
                          val hi:Double,
                          val si:Double,
                          val st:Double
) : KafkaRecord(key, "cpu-stats-record")
