package ti.lsm.henrique.io.linereader

import ti.lsm.henrique.io.ComputerIdentifier
import ti.lsm.henrique.io.linereader.exceptions.CannotReadLineException
import ti.lsm.henrique.model.TasksRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksLineReader : TopReader<TasksRecord> {

    @Inject
    lateinit var computerIdentifier:ComputerIdentifier

    override val regex: Regex = Regex("Tasks:\\s+(\\d+)\\s+total,\\s+(\\d+)\\s+running,\\s+(\\d+) sleeping,\\s+(\\d+)\\s+stopped,\\s+(\\d+)\\s+zombie")

    override fun read(line: String): TasksRecord {
        val matchResult = regex.find(line) ?: throw CannotReadLineException(line)

        val groups = matchResult.groups

        return TasksRecord(
                key = computerIdentifier.id,
                total = groups[1]?.value?.toInt() ?: 0,
                running = groups[2]?.value?.toInt() ?: 0,
                sleeping = groups[3]?.value?.toInt() ?: 0,
                stopped = groups[4]?.value?.toInt() ?: 0,
                zombie = groups[5]?.value?.toInt() ?: 0
        )
    }

}