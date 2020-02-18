package ti.lsm.henrique

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("ti.lsm.henrique")
                .mainClass(Application.javaClass)
                .start()
    }
}