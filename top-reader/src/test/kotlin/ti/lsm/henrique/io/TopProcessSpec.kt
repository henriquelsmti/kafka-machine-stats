package ti.lsm.henrique.io

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import io.reactivex.Flowable
import ti.lsm.henrique.io.process.MockProcessExecutor
import ti.lsm.henrique.model.KafkaRecord
import ti.lsm.henrique.model.LoadAverage
import ti.lsm.henrique.model.LostRecord
import ti.lsm.henrique.model.TopRecord
import java.time.Duration
import java.time.LocalTime

class TopProcessSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var topProcessReader: TopProcessReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run()
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        topProcessReader = context.getBean(TopProcessReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }


    @Test
    fun testTopProcessTopRecordProduce() {
        val lostLine = "top lost"
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        mockProcessExecutor.flowable = Flowable.just(
                "top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78",
                lostLine
        )
        val stream = topProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }
        list.isNotEmpty().shouldBeTrue()
        val topRecord = list.find { it is TopRecord }
        topRecord.shouldNotBeNull()
        topRecord.shouldBe(TopRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(20, 23, 27),
                upTime = Duration.ofMinutes(16),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.54,
                        lastFiveMinutes = 0.86,
                        lastFifteenMinutes = 0.78
                )
        ))
        val lostRecord = list.find { it is LostRecord }
        lostRecord.shouldNotBeNull()
        lostRecord.shouldBe(LostRecord(key = computerIdentifier.id, line = lostLine))

    }
}