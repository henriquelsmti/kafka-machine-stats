package ti.lsm.henrique.service

import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.TestConfigs

class MeasureSeviceSpec : AnnotationSpec() {

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
    fun testGigaToKilo() {
        val measureService = context.getBean(MeasureService::class.java)
        measureService.convertToKiloByte("1000").shouldBe(1000)
        measureService.convertToKiloByte("2.5g").shouldBe(2621440)
    }
}