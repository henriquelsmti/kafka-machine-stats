package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.model.KafkaRecord

interface LineReader<out T : KafkaRecord> {
    fun read(line: String): T
}