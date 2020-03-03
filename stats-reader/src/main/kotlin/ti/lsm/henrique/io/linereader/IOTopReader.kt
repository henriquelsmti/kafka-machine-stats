package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.model.KafkaRecord

interface IOTopReader<out T : KafkaRecord> : LineReader<T>