package ti.lsm.henrique

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut

object Application {

    lateinit var context:ApplicationContext

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("ti.lsm.henrique")
                .mainClass(Application.javaClass)
                .start()
    }
}