package ti.lsm.henrique.io

import io.reactivex.Flowable
import javax.inject.Singleton

@Singleton
class TopProcess {


    fun init():Flowable<Any>{
        val executor  = ProcessExecutor()
    }

}