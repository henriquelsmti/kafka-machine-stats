package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.DiskStatsRecord
import ti.lsm.henrique.model.TasksRecord
import ti.lsm.henrique.service.MeasureService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiskStatsLineReader : IOTopReader<DiskStatsRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("\\s*Total\\s+DISK\\s+READ:\\s+([\\d.]+)\\s+K/s\\s+\\|\\s+Total\\s+DISK\\s+WRITE:\\s+([\\d.]+)\\s+K/s\\s*")

    override fun read(line: String): DiskStatsRecord {
        val matchResult = regex.find(line) ?: throw CannotReadLineException(line)

        val groups = matchResult.groups

        return DiskStatsRecord(
                key = computerIdentifier.id,
                totalRead = groups[1]?.value?.toDouble() ?: 0.0,
                totalWrite = groups[2]?.value?.toDouble() ?: 0.0
        )
    }

}