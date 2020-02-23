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
import ti.lsm.henrique.model.CpuStatsRecord

class CpuStatsLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier:ComputerIdentifier
    lateinit var cpuStatsLineReader:CpuStatsLineReader
    
    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        cpuStatsLineReader = context.getBean(CpuStatsLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderStats() {
        val line = "%Cpu(s):  1.5 us,  1.0 sy,  1.0 ni, 98.5 id,  1.0 wa,  1.0 hi,  1.0 si,  1.0 st"
        val result = cpuStatsLineReader.read(line)

        result.shouldBe(CpuStatsRecord(
                key = computerIdentifier.id,
                us = 1.5,
                sy = 1.0,
                ni = 1.0,
                id = 98.5,
                wa = 1.0,
                hi = 1.0,
                si = 1.0,
                st = 1.0
        ))
    }

    @Test
    fun testReaderStats2() {
        val line = "%Cpu(s):  0.0 us,  0.0 sy,  0.0 ni, 0.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st"
        val result = cpuStatsLineReader.read(line)

        result.shouldBe(CpuStatsRecord(
                key = computerIdentifier.id,
                us = 0.0,
                sy = 0.0,
                ni = 0.0,
                id = 0.0,
                wa = 0.0,
                hi = 0.0,
                si = 0.0,
                st = 0.0
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            cpuStatsLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}