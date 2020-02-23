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
import ti.lsm.henrique.model.MemoryStatsRecord

class MemoryStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var memoryStatsLineReader: MemoryStatsLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        memoryStatsLineReader = context.getBean(MemoryStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderStats() {
        val line = "MiB Mem :  16032.1 total,  11889.8 free,   2446.9 used,   1695.4 buff/cache"
        val result = memoryStatsLineReader.read(line)

        result.shouldBe(MemoryStatsRecord(
                key = computerIdentifier.id,
                total = 16032.1,
                free = 11889.8,
                used = 2446.9,
                buffCache = 1695.4
        ))
    }

    @Test
    fun testReaderStats2() {
        val line = "%MiB Mem :  0.0 total,  0.0 free,   0.0 used,   0.0 buff/cache"
        val result = memoryStatsLineReader.read(line)

        result.shouldBe(MemoryStatsRecord(
                key = computerIdentifier.id,
                total = 0.0,
                free = 0.0,
                used = 0.0,
                buffCache = 0.0
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            memoryStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}