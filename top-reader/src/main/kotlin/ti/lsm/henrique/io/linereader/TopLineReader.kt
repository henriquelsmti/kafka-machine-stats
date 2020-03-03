package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.LoadAverage
import ti.lsm.henrique.model.SystemStatsRecord
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopLineReader : TopReader<SystemStatsRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("top\\s-\\s(\\d{2}:\\d{2}:\\d{2})\\sup\\s+((\\d+)\\s+days?,\\s+)?((\\d+)\\smin,\\s+)?((\\d+:\\d+),\\s+)?(\\d+)\\susers?,\\s+load average:\\s+([\\d.]+),\\s+([\\d.]+),\\s+([\\d.]+)\\s*")

    override fun read(line: String): SystemStatsRecord {
        val matchResult = regex.find(line) ?: throw CannotReadLineException(line)
        val groups = matchResult.groups

        val load = LoadAverage(
                lastMinute = groups[9]?.value?.toDouble() ?: 0.0,
                lastFiveMinutes = groups[10]?.value?.toDouble() ?: 0.0,
                lastFifteenMinutes = groups[11]?.value?.toDouble() ?: 0.0
        )

        val time = LocalTime.parse(groups[1]?.value ?: "00:00:00",
                DateTimeFormatter.ISO_LOCAL_TIME)


        var upTime = Duration.ZERO

        if (groups[2]?.value != null) {
            upTime = upTime.plusDays((groups[3]?.value ?: "0").toLong())
        }

        if (groups[4]?.value != null) {
            upTime = upTime.plusMinutes((groups[5]?.value ?: "0").toLong())
        }else{
            val minSec = (groups[7]?.value ?: "0:0").split(":")
            upTime = upTime.plusHours(minSec[0].toLong())
            upTime = upTime.plusMinutes(minSec[1].toLong())
        }


        val users = (groups[8]?.value ?: "1").toInt()

        return SystemStatsRecord(
                key = computerIdentifier.id,
                time = time,
                upTime = upTime,
                users = users,
                loadAverage = load
        )
    }

}