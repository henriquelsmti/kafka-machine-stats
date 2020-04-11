package ti.lsm.henrique.streams

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic

@CompileStatic
class Application {

    public static ApplicationContext context

    static void main(String[] args) {
        context = Micronaut.build(args)
                .packages("ti.lsm.henrique")
                .mainClass(Application)
                .start()
    }
}