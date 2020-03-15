package ti.lsm.henrique.streams

import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class Application {
    static void main(String[] args) {
        Micronaut.build(args)
                .packages("ti.lsm.henrique")
                .mainClass(Application)
                .start()
    }
}