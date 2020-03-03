package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.model.KafkaRecord

interface TopReader<out T : KafkaRecord> : LineReader<T>