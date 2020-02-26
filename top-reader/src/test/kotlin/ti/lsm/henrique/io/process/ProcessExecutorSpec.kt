package ti.lsm.henrique.io.process

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import ti.lsm.henrique.io.exceptions.IOException

class ProcessExecutorSpec : AnnotationSpec() {

    @Test
    fun testProcessOutputFlowable() {
        val executor = ProcessExecutorImp()
        executor.lineEmitter = LineEmitter()
        val list = mutableListOf<String>()
        val flowable = executor.start(listOf("ping", "-c 4", "localhost"))
        flowable.subscribe {
            list.add(it)
        }
        while (!executor.isStoped()){
            Thread.sleep(1000)
            println("waiting...")
        }
        list.isNotEmpty().shouldBeTrue()
        executor.close()
    }

    @Test
    fun testFallStartAnyTimes() {
        val executor = ProcessExecutorImp()
        executor.lineEmitter = LineEmitter()
        shouldThrow<IOException> {
            executor.start(listOf("ping", "-c 4", "localhost"))
            executor.start(listOf("ping", "-c 4", "localhost"))
        }
        executor.close()
    }
}