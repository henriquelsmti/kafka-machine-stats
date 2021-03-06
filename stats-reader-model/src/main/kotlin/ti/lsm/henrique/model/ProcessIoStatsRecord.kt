package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class ProcessIoStatsRecord(
        override val key: String,
        val pid:Int,
        val prio:String,
        val user:String,
        val diskRead: Double,
        val diskWrite: Double,
        val swapin: Double,
        val io: Double,
        val command:String
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "process-disk-stats-record"
    }
}