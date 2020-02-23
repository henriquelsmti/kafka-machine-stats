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
import ti.lsm.henrique.model.SwapStatsRecord

class SwapStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var swapStatsLineReader: SwapStatsLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        swapStatsLineReader = context.getBean(SwapStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderStats() {
        val line = "MiB Swap:   2048.0 total,   2048.0 free,      1.1 used.  13250.3 avail Mem"
        val result = swapStatsLineReader.read(line)

        result.shouldBe(SwapStatsRecord(
                key = computerIdentifier.id,
                total = 2048.0,
                free = 2048.0,
                used = 1.1,
                availMem = 13250.3
        ))
    }

    @Test
    fun testReaderStats2() {
        val line = "MiB Swap:   0.0 total,   0.0 free,      0.0 used.  0.0 avail Mem"
        val result = swapStatsLineReader.read(line)

        result.shouldBe(SwapStatsRecord(
                key = computerIdentifier.id,
                total = 0.0,
                free = 0.0,
                used = 0.0,
                availMem = 0.0
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            swapStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}