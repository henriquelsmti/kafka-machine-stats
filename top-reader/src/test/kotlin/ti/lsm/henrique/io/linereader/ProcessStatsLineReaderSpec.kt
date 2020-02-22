package ti.lsm.henrique.io.linereader

import io.kotlintest.matchers.startWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import io.micronaut.context.ApplicationContext
import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.TestConfigs
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.ProcessStatsRecord
import java.time.Duration

class ProcessStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var processStatsLineReader: ProcessStatsLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        processStatsLineReader = context.getBean(ProcessStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderTasks() {
        val line = "9196 henriqu+  20   0  678416 156956  80068 S  20.9   1.0   1:47.44 chrome"
        val result = processStatsLineReader.read(line)

        val timePlus = Duration.ofMinutes(1)
                .plusSeconds(47)
                .plusMillis(440)

        result.shouldBe(ProcessStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${9196}}""",
                computerIdentifier = computerIdentifier.id,
                pid = 9196,
                user = "henriqu+",
                pr = 20,
                ni = 0,
                virt = 678416,
                res = 156956,
                shr = 80068,
                s = "S",
                cpu = 20.9,
                mem = 1.0,
                timePlus = timePlus,
                command = "chrome"
        ))
    }

    @Test
    fun testReaderTasks2() {
        val line = "0 root  0   0  0 0  0 Y  0.0   0.0   0:0.0 top"
        val result = processStatsLineReader.read(line)

        val timePlus = Duration.ofMinutes(0)

        result.shouldBe(ProcessStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${0}}""",
                computerIdentifier = computerIdentifier.id,
                pid = 0,
                user = "root",
                pr = 0,
                ni = 0,
                virt = 0,
                res = 0,
                shr = 0,
                s = "Y",
                cpu = 0.0,
                mem = 0.0,
                timePlus = timePlus,
                command = "top"
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            processStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}