package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class SwapStatsRecord(override val key: String,
                           val total:Double,
                           val free:Double,
                           val used:Double,
                           val availMem:Double
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "swap-stats-record"
    }
}
