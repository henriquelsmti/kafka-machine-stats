package ti.lsm.henrique.io

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.startWith
import io.kotlintest.should
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.io.exceptions.IOException
import ti.lsm.henrique.io.linereader.exceptions.LineReaderException

class ProcessExecutorSpec : AnnotationSpec() {

    @Test
    fun testProcessOutputFlowable() {
        val context = ApplicationContext.run()
        val executor = context.getBean(ProcessExecutor::class.java)

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
        context.close()
    }

    @Test
    fun testFallStartAnyTimes() {
        val context = ApplicationContext.run()
        val executor = context.getBean(ProcessExecutor::class.java)

        val exception = shouldThrow<IOException> {
            executor.start("ping", "-c 4", "localhost")
            executor.start("ping", "-c 4", "localhost")

        }
        executor.close()
        context.close()
    }
}