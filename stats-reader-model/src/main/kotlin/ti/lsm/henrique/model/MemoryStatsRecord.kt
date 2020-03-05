package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class MemoryStatsRecord(override val key: String,
                             val total:Double,
                             val free:Double,
                             val used:Double,
                             val buffCache:Double
) : KafkaRecord(key, "memory-stats-record")
