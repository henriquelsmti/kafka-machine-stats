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
import ti.lsm.henrique.model.TasksRecord

class TasksLineReaderSpec : AnnotationSpec() {

    lateinit var context: ApplicationContext

    lateinit var computerIdentifier: ComputerIdentifier
    lateinit var tasksLineReader: TasksLineReader

    @BeforeClass
    fun before() {
        context = ApplicationContext.run(TestConfigs.config)
        computerIdentifier = context.getBean(ComputerIdentifier::class.java)
        tasksLineReader = context.getBean(TasksLineReader::class.java)
    }

    @AfterClass
    fun after() {
        context.close()
    }

    @Test
    fun testReaderTasks() {
        val line = "Tasks: 268 total,   1 running, 267 sleeping,   0 stopped,   0 zombie"
        val result = tasksLineReader.read(line)

        result.shouldBe(TasksRecord(
                key = computerIdentifier.id,
                total = 268,
                running = 1,
                sleeping = 267,
                stopped = 0,
                zombie = 0
        ))
    }

    @Test
    fun testReaderTasks2() {
        val line = "Tasks: 268 total,   8 running, 267 sleeping,   10 stopped,   10 zombie"
        val result = tasksLineReader.read(line)

        result.shouldBe(TasksRecord(
                key = computerIdentifier.id,
                total = 268,
                running = 8,
                sleeping = 267,
                stopped = 10,
                zombie = 10
        ))
    }

    @Test
    fun testReaderFault() {
        val line = "Tasks - Error"
        val exception = shouldThrow<CannotReadLineException> {
            tasksLineReader.read(line)
        }
        exception.message should startWith("Cannot read the line:")
    }
}