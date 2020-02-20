package ti.lsm.henrique.io

import io.reactivex.Flowable
import ti.lsm.henrique.model.KafkaRecord

interface ProcessReader {

    fun init(): Flowable<KafkaRecord>
}