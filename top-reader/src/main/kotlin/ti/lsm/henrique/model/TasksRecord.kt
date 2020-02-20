package ti.lsm.henrique.model

data class TasksRecord(override val key: String,
                       val total:Int,
                       val running:Int,
                       val sleeping:Int,
                       val stopped:Int,
                       val zombie:Int
) : KafkaRecord(key, "tasks-record")
