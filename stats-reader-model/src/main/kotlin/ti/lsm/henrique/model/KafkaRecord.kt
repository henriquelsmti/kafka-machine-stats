package ti.lsm.henrique.model

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class KafkaRecord(
        open val key: String,
        @get:JsonIgnore
        val topic: String
)