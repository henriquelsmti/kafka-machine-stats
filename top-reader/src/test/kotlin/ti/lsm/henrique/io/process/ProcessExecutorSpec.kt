package ti.lsm.henrique.io.process

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.io.exceptions.IOException

class ProcessExecutorSpec : AnnotationSpec() {

    @Test
    fun testProcessOutputFlowable() {
        val executor = ProcessExecutorImp()

        val list = mutableListOf<String>()
        val flowable = executor.start("ping", "-c 4", "localhost")
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

        shouldThrow<IOException> {
            executor.start("ping", "-c 4", "localhost")
            executor.start("ping", "-c 4", "localhost")
        }
        executor.close()
    }
}