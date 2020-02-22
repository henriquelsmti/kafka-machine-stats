package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.MemoryStatsRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryStatsLineReader : TopReader<MemoryStatsRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("MiB\\s+Mem\\s+:\\s+([\\d.]+)\\s+total,\\s+([\\d.]+)\\s+free,\\s+([\\d.]+)\\s+used,\\s+([\\d.]+)\\s+buff/cache\\s*")

    override fun read(line: String): MemoryStatsRecord {
        val matchResult = regex.find(line) ?: throw CannotReadLineException(line)

        val groups = matchResult.groups

        return MemoryStatsRecord(
                key = computerIdentifier.id,
                total = groups[1]?.value?.toDouble() ?: 0.0,
                free = groups[2]?.value?.toDouble() ?: 0.0,
                used = groups[3]?.value?.toDouble() ?: 0.0,
                buffCache = groups[4]?.value?.toDouble() ?: 0.0
        )
    }

}