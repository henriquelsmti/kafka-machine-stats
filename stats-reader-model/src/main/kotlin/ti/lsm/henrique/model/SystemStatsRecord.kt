package ti.lsm.henrique.model

import java.time.Duration
import java.time.LocalTime

data class SystemStatsRecord(override val key: String,
                             val time: LocalTime,
                             val upTime: Duration,
                             val users: Int,
                             val loadAverage: LoadAverage
) : KafkaRecord(key, "top-record")
