package ti.lsm.henrique.io.linereader

import io.kotlintest.matchers.startWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.TestConfigs
import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.ProcessIoStatsRecord

class ProcessIoStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var processIoStatsLineReader: ProcessIoStatsLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        processIoStatsLineReader = context.getBean(ProcessIoStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderStats() {
        val line = " 6871 be/4 root        0.00 K/s    0.00 K/s  0.00 %  0.00 % [kworker/6:0-events]"
        val result = processIoStatsLineReader.read(line)

        result.shouldBe(ProcessIoStatsRecord(
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
    fun testReaderStats2() {
        val line = "6871 be/4 root        100.00 K/s    0.50 K/s  11.00 %  0.05 % java "
        val result = processIoStatsLineReader.read(line)

        result.shouldBe(ProcessIoStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${6871}}""",
                pid = 6871,
                prio = "be/4",
                user = "root",
                diskRead = 100.0,
                diskWrite = 0.50,
                swapin = 11.0,
                io = 0.05,
                command = "java"
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            processIoStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}