package ti.lsm.henrique.model

abstract class KafkaRecord(open val key: String, val topic: String)