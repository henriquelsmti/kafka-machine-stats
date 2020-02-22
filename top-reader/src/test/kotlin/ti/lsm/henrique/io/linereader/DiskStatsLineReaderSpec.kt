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
import ti.lsm.henrique.model.DiskStatsRecord

class DiskStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var diskStatsLineReader: DiskStatsLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        diskStatsLineReader = context.getBean(DiskStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderTasks() {
        val line = "Total DISK READ:         0.00 K/s | Total DISK WRITE:         0.00 K/s"
        val result = diskStatsLineReader.read(line)

        result.shouldBe(DiskStatsRecord(
                key = computerIdentifier.id,
                totalWrite = 0.0,
                totalRead = 0.0
        ))
    }

    @Test
    fun testReaderTasks2() {
        val line = "Total DISK READ:         100.00 K/s | Total DISK WRITE:         0.50 K/s"
        val result = diskStatsLineReader.read(line)

        result.shouldBe(DiskStatsRecord(
                key = computerIdentifier.id,
                totalWrite = 0.50,
                totalRead = 100.0
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            diskStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}