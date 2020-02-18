package ti.lsm.henrique.io

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.AnnotationSpec

class ProcessExecutorSpec : AnnotationSpec() {

    @Test
    fun testProcessOutputFlowable() {
        val executor = ProcessExecutor("ping", "-c 4", "localhost")

        val list = mutableListOf<String>()
        val flowable = executor.start()
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
}