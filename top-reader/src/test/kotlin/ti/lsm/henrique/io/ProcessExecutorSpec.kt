package ti.lsm.henrique.io

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext

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
}