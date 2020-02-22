package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.ProcessStatsRecord
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessStatsLineReader : TopReader<ProcessStatsRecord> {

    @Inject
    lateinit var computerIdentifier: ComputerIdentifier

    override val regex: Regex = Regex("(\\d+)\\s+(.*)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\w+)\\s+([.\\d]+)\\s+([.\\d]+)\\s+(\\d+:\\d+\\.\\d+)\\s+(.*)")

    override fun read(line: String): ProcessStatsRecord {
        val matchResult = regex.find(line) ?: throw throw CannotReadLineException(line)

        val groups = matchResult.groups

        val pid = groups[1]?.value?.toInt() ?: 0

        val nuns = (groups[11]?.value ?: "0:0.0").split("[^\\d]".toRegex())
        val timePlus = Duration.ofMinutes(nuns[0].toLong())
                .plusSeconds(nuns[1].toLong())
                .plusMillis(nuns[2].toLong() * 10)

        return ProcessStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${pid}}""",
                computerIdentifier = computerIdentifier.id,
                pid = pid,
                user = groups[2]?.value?.trim() ?: "",
                pr = groups[3]?.value?.toInt() ?: 0,
                ni = groups[4]?.value?.toInt() ?: 0,
                virt = groups[5]?.value?.toInt() ?: 0,
                res = groups[6]?.value?.toInt() ?: 0,
                shr = groups[7]?.value?.toInt() ?: 0,
                s = groups[8]?.value ?: "",
                cpu = groups[9]?.value?.toDouble() ?: 0.0,
                mem = groups[10]?.value?.toDouble() ?: 0.0,
                timePlus = timePlus,
                command = groups[12]?.value ?: ""
        )
    }

}