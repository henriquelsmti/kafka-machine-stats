package ti.lsm.henrique.service

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.TestConfigs
import ti.lsm.henrique.io.process.MockProcessExecutor
import ti.lsm.henrique.model.*
import java.time.Duration
import java.time.LocalTime

class MainSeviceSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext


    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
    }

    @AfterClass
    fun after() {
        context.close()
    }


    @Test
    fun testKafkaProduce() {
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext("top lost")
    }
}