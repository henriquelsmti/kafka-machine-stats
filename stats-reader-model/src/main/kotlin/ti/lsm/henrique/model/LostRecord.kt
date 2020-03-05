package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class LostRecord(override val key: String, val line: String) : KafkaRecord(key, "lost-record")