package ti.lsm.henrique.model

import java.time.Duration
import java.time.LocalTime

import io.micronaut.core.annotation.Introspected

@Introspected
data class SystemStatsRecord(override val key: String,
                             val time: LocalTime,
                             val upTime: Duration,
                             val users: Int,
                             val loadAverage: LoadAverage
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "system-stats-record"
    }
}
