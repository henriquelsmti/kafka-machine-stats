package ti.lsm.henrique.io.process

import io.micronaut.context.annotation.Replaces
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Replaces(ProcessExecutorImp::class)
@Singleton
class MockProcessExecutor : ProcessExecutor {

    val observable = PublishSubject.create<String>()
    private val flowable: Flowable<String> =  observable.toFlowable(BackpressureStrategy.MISSING)



    override fun start(vararg command: String): Flowable<String> {
        return flowable
    }

    override fun isStoped(): Boolean {
        return false
    }

    override fun close() {

    }
}