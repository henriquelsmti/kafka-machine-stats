package ti.lsm.henrique.io

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.TestConfigs
import ti.lsm.henrique.io.process.MockProcessExecutor
import ti.lsm.henrique.model.*

class IOTopProcessSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var ioTopProcessReader: IOTopProcessReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        ioTopProcessReader = context.getBean(IOTopProcessReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }


    @Test
    fun testIoTopRecordProduce() {
        val lostLine = "top lost"
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        val stream = ioTopProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }

        mockProcessExecutor.observable.onNext(" 6871 be/4 root        0.00 K/s    0.00 K/s  0.00 %  0.00 % [kworker/6:0-events]")
        mockProcessExecutor.observable.onNext(lostLine)

        list.isNotEmpty().shouldBeTrue()
        val topRecord = list.find { it is ProcessIoStatsRecord }
        topRecord.shouldNotBeNull()
        topRecord.shouldBe(ProcessIoStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${6871}}""",
                pid = 6871,
                prio = "be/4",
                user = "root",
                diskRead = 0.0,
                diskWrite = 0.0,
                swapin = 0.0,
                io = 0.0,
                command = "[kworker/6:0-events]"
        ))
        val lostRecord = list.find { it is LostRecord }
        lostRecord.shouldNotBeNull()
        lostRecord.shouldBe(LostRecord(key = computerIdentifier.id, line = lostLine))
    }

    @Test
    fun testIoTopProcessProduce() {
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        val stream = ioTopProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }
        mockProcessExecutor.observable.onNext("Total DISK READ:         0.00 K/s | Total DISK WRITE:         0.00 K/s")
        mockProcessExecutor.observable.onNext(" 6871 be/4 root        0.00 K/s    0.00 K/s  0.00 %  0.00 % [kworker/6:0-events]")
        list.isNotEmpty().shouldBeTrue()
        val diskStatsRecord = list.find { it is DiskStatsRecord }
        diskStatsRecord.shouldNotBeNull()
        diskStatsRecord.shouldBe(DiskStatsRecord(
                key = computerIdentifier.id,
                totalWrite = 0.0,
                totalRead = 0.0
        ))
        val tasksRecord = list.find { it is ProcessIoStatsRecord }
        tasksRecord.shouldNotBeNull()
        tasksRecord.shouldBe(ProcessIoStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${6871}}""",
                pid = 6871,
                prio = "be/4",
                user = "root",
                diskRead = 0.0,
                diskWrite = 0.0,
                swapin = 0.0,
                io = 0.0,
                command = "[kworker/6:0-events]"
        ))
    }

    @Test
    fun testIoTopProcessIgnoreHeaderProcessLine() {
        val mockProcessExecutor = context.getBean(MockProcessExecutor::class.java)
        val stream = ioTopProcessReader.init()
        val list = mutableListOf<KafkaRecord>()
        stream.subscribe {
            list.add(it)
        }
        mockProcessExecutor.observable.onNext("Total DISK READ:         0.00 K/s | Total DISK WRITE:         0.00 K/s")
        mockProcessExecutor.observable.onNext("      PID  PRIO  USER     DISK READ  DISK WRITE  SWAPIN     IO>    COMMAND")
        mockProcessExecutor.observable.onNext("PID  PRIO  USER     DISK READ  DISK WRITE  SWAPIN     IO>    COMMAND    ")

        list.size.shouldBe(1)
        val diskStatsRecord = list[0]
        diskStatsRecord.shouldNotBeNull()
        diskStatsRecord.shouldBe(DiskStatsRecord(
                key = computerIdentifier.id,
                totalWrite = 0.0,
                totalRead = 0.0
        ))
    }
}