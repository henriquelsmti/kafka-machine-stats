package ti.lsm.henrique.model


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
) : KafkaRecord(key, "process-disk-stats-record")