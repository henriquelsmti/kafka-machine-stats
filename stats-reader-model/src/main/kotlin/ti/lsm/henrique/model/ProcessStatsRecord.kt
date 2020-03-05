package ti.lsm.henrique.model

import java.time.Duration

import io.micronaut.core.annotation.Introspected

@Introspected
data class ProcessStatsRecord(override val key: String,
                              val computerIdentifier:String,
                              val pid:Int,
                              val user:String,
                              val pr:String,
                              val ni:Int,
                              val virt:Int,
                              val res:Int,
                              val shr:Int,
                              val s:String,
                              val cpu:Double,
                              val mem:Double,
                              val timePlus:Duration,
                              val command:String
) : KafkaRecord(key, "process-stats-record")
