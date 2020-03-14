package ti.lsm.henrique.model
import io.micronaut.core.annotation.Introspected

@Introspected
data class CpuStatsRecord(override val key: String,
                          val us:Double,
                          val sy:Double,
                          val ni:Double,
                          val id:Double,
                          val wa:Double,
                          val hi:Double,
                          val si:Double,
                          val st:Double
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "cpu-stats-record"
    }
}
