package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class DiskStatsRecord(
        override val key: String,
        val totalRead: Double,
        val totalWrite: Double
) : KafkaRecord(key, "disk-stats-record")