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
import ti.lsm.henrique.model.LoadAverage
import ti.lsm.henrique.model.SystemStatsRecord
import java.time.Duration
import java.time.LocalTime

class TopLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var topLineReader: TopLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        topLineReader = context.getBean(TopLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderUpMinutes() {

        val line = "top - 20:23:27 up 16 min,  1 user,  load average: 0.54, 0.86, 0.78"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
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

    @Test
    fun testReaderUpHoursMinutes() {

        val line = "top - 21:19:27 up  2:28,  1 users,  load average: 0.73, 0.81, 0.77"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(21, 19, 27),
                upTime = Duration.ofHours(2).plusMinutes(28),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.73,
                        lastFiveMinutes = 0.81,
                        lastFifteenMinutes = 0.77
                )
        ))
    }

    @Test
    fun testReaderUpDayHoursMinutes() {

        val line = "top - 21:19:27 up 1 day, 2:28,  1 users,  load average: 0.73, 0.81, 0.77"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(21, 19, 27),
                upTime = Duration.ofDays(1).plusHours(2).plusMinutes(28),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.73,
                        lastFiveMinutes = 0.81,
                        lastFifteenMinutes = 0.77
                )
        ))
    }

    @Test
    fun testReaderUpDaysHoursMinutes() {

        val line = "top - 21:19:27 up 2 days, 2:28,  1 users,  load average: 0.73, 0.81, 0.77"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(21, 19, 27),
                upTime = Duration.ofDays(2).plusHours(2).plusMinutes(28),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.73,
                        lastFiveMinutes = 0.81,
                        lastFifteenMinutes = 0.77
                )
        ))
    }

    @Test
    fun testReaderUpDayMinutes() {

        val line = "top - 20:23:27 up 1 day, 16 min,  1 user,  load average: 0.54, 10.86, 0.78"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(20, 23, 27),
                upTime = Duration.ofDays(1).plusMinutes(16),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.54,
                        lastFiveMinutes = 10.86,
                        lastFifteenMinutes = 0.78
                )
        ))
    }

    @Test
    fun testReaderUpDaysMinutes() {

        val line = "top - 20:23:27 up 2 days, 16 min,  1 user,  load average: 1.54, 10.86, 0.78"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(20, 23, 27),
                upTime = Duration.ofDays(2).plusMinutes(16),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 1.54,
                        lastFiveMinutes = 10.86,
                        lastFifteenMinutes = 0.78
                )
        ))
    }

    @Test
    fun testReaderUpDaysMinutesManyUsers() {

        val line = "top - 20:23:27 up 2 days, 16 min,  10 user,  load average: 1.54, 10.86, 0.78"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(20, 23, 27),
                upTime = Duration.ofDays(2).plusMinutes(16),
                users = 10,
                loadAverage = LoadAverage(
                        lastMinute = 1.54,
                        lastFiveMinutes = 10.86,
                        lastFifteenMinutes = 0.78
                )
        ))
    }

    @Test
    fun testReaderUpDays2Hours2Minutes() {

        val line = "top - 21:19:27 up 20 days, 20:28,  1 users,  load average: 0.73, 0.81, 0.77"
        val result = topLineReader.read(line)
        result.shouldBe(SystemStatsRecord(
                key = computerIdentifier.id,
                time = LocalTime.of(21, 19, 27),
                upTime = Duration.ofDays(20).plusHours(20).plusMinutes(28),
                users = 1,
                loadAverage = LoadAverage(
                        lastMinute = 0.73,
                        lastFiveMinutes = 0.81,
                        lastFifteenMinutes = 0.77
                )
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "top - Error"
        val exception = shouldThrow<CannotReadLineException> {
            topLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}