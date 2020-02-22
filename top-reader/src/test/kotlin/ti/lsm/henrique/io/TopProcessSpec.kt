package ti.lsm.henrique.io

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

class TopProcessSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var topProcessReader: TopProcessReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
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
        val stream = topProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }

        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext(lostLine)

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

    @Test
    fun testTopProcessProduce() {
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        val stream = topProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }
        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext("Tasks: 268 total,   1 running, 267 sleeping,   0 stopped,   0 zombie")
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
        val tasksRecord = list.find { it is TasksRecord }
        tasksRecord.shouldNotBeNull()
        tasksRecord.shouldBe(TasksRecord(
                key = computerIdentifier.id,
                total = 268,
                running = 1,
                sleeping = 267,
                stopped = 0,
                zombie = 0
        ))
    }

    @Test
    fun testTopProcessIgnoreHeaderProcessLine() {
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        val stream = topProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }
        mockProcessExecutor.observable.onNext("top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78")
        mockProcessExecutor.observable.onNext("  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND")
        mockProcessExecutor.observable.onNext("PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND")

        list.size.shouldBe(1)
        val topRecord = list[0]
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
    }
}