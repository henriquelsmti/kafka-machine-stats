package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.DiskStatsRecord
import ti.lsm.henrique.model.ProcessIoStatsRecord
import ti.lsm.henrique.model.TasksRecord
import ti.lsm.henrique.service.MeasureService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessIoStatsLineReader : IOTopReader<ProcessIoStatsRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("\\s*(\\d+)\\s+(.*?)\\s+(.*)\\s+([\\d.]+)\\s+K/s\\s+([\\d.]+)\\s+K/s\\s+([\\d.]+)\\s+%\\s+([\\d.]+)\\s+%\\s+(.*)\\s*")

    override fun read(line: String): ProcessIoStatsRecord {
        val matchResult = regex.find(line) ?: throw CannotReadLineException(line)

        val groups = matchResult.groups

        val pid = groups[1]?.value?.toInt() ?: 0
        return ProcessIoStatsRecord(
                key = """{"computerIdentifier":"${computerIdentifier.id}", "pid": ${pid}}""",
                pid = pid,
                prio = groups[2]?.value ?: "",
                user = groups[3]?.value?.trim() ?: "",
                diskRead = groups[4]?.value?.toDouble() ?: 0.0,
                diskWrite = groups[5]?.value?.toDouble() ?: 0.0,
                swapin = groups[6]?.value?.toDouble() ?: 0.0,
                io = groups[7]?.value?.toDouble() ?: 0.0,
                command = groups[8]?.value?.trim() ?: ""
        )
    }

}