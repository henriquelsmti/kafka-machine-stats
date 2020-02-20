package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.LineReaderException
import ti.lsm.henrique.model.LoadAverage
import ti.lsm.henrique.model.TopRecord
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopLineReader : TopReader<TopRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    companion object {
        val regex = Regex("top\\s-\\s(\\d{2}:\\d{2}:\\d{2})\\sup\\s+((\\d+)\\s+days?,\\s+)?((\\d+)\\smin,\\s+)?((\\d+:\\d+),\\s+)?(\\d+)\\susers?,\\s+load average:\\s+([\\d.]+),\\s+([\\d.]+),\\s+([\\d.]+)")
    }

    override fun read(line: String): TopRecord {
        val matchResult = regex.find(line) ?: throw LineReaderException("it is not possible to read the line: $line")
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

        return TopRecord(
                key = computerIdentifier.id,
                time = time,
                upTime = upTime,
                users = users,
                loadAverage = load
        )
    }

}