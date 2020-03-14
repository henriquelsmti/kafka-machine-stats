package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class TasksRecord(override val key: String,
                       val total:Int,
                       val running:Int,
                       val sleeping:Int,
                       val stopped:Int,
                       val zombie:Int
) : KafkaRecord(key, topic) {
    companion object {
        const val topic = "tasks-record"
    }
}

