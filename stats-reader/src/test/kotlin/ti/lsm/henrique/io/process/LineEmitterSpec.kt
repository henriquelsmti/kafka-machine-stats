package ti.lsm.henrique.io.process

import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import java.io.BufferedReader
import java.io.InputStreamReader

class LineEmitterSpec : AnnotationSpec() {

    @Test
    fun testRegisty() {
        val lineEmitter = LineEmitter()
        val list = mutableListOf<String>()
        val observable = PublishSubject.create<String>()
        val flowable = observable.toFlowable(BackpressureStrategy.MISSING)
        flowable.subscribe {
            list.add(it)
        }
        val txt = "1\n2\n3"
        val reader = BufferedReader(InputStreamReader(txt.byteInputStream()))
        lineEmitter.registre(reader, observable)
        Thread.sleep(1000)

        list.shouldBe(listOf("1", "2", "3"))

    }

    @Test
    fun testUnregister() {
        val lineEmitter = LineEmitter()
        val list = mutableListOf<String>()
        val observable = PublishSubject.create<String>()
        val flowable = observable.toFlowable(BackpressureStrategy.MISSING)
        flowable.subscribe {
            list.add(it)
        }
        var txt = "1\n2\n3"
        var reader = BufferedReader(InputStreamReader(txt.byteInputStream()))
        lineEmitter.registre(reader, observable)

        txt = "4\n5\n6"
        reader = BufferedReader(InputStreamReader(txt.byteInputStream()))
        lineEmitter.registre(reader, observable)

        txt = "7\n8\n9"
        reader = BufferedReader(InputStreamReader(txt.byteInputStream()))
        lineEmitter.registre(reader, observable)
        Thread.sleep(1000)

        lineEmitter.unregister(reader)

        lineEmitter.getReadersAndFlowables().size.shouldBe(2)

        list.toSet().shouldBe(setOf("1", "2", "3", "4", "5", "6", "7", "8", "9"))
    }
}