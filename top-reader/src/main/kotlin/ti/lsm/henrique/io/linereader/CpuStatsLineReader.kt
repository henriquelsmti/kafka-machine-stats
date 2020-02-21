package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.LineReaderException
import ti.lsm.henrique.model.CpuStatsRecord
import ti.lsm.henrique.model.TasksRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CpuStatsLineReader : TopReader<CpuStatsRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("%Cpu\\(s\\):\\s+([\\d.]+)\\s+us,\\s+([\\d.]+)\\s+sy,\\s+([\\d.]+)\\s+ni,\\s+([\\d.]+)\\s+id,\\s+([\\d.]+)\\s+wa,\\s+([\\d.]+)\\s+hi,\\s+([\\d.]+)\\s+si,\\s+([\\d.]+)\\s+st")

    override fun read(line: String): CpuStatsRecord {
        val matchResult = regex.find(line) ?: throw LineReaderException("it is not possible to read the line: $line")

        val groups = matchResult.groups

        return CpuStatsRecord(
                key = computerIdentifier.id,
                us = groups[1]?.value?.toDouble() ?: 0.0,
                sy = groups[2]?.value?.toDouble() ?: 0.0,
                ni = groups[3]?.value?.toDouble() ?: 0.0,
                id = groups[4]?.value?.toDouble() ?: 0.0,
                wa = groups[5]?.value?.toDouble() ?: 0.0,
                hi = groups[6]?.value?.toDouble() ?: 0.0,
                si = groups[7]?.value?.toDouble() ?: 0.0,
                st = groups[8]?.value?.toDouble() ?: 0.0
        )
    }

}