package ti.lsm.henrique.service

import javax.inject.Singleton

@Singleton
class MeasureService {


    fun convertToKiloByte(value: String): Int {
        return if (value.contains("g")) {
            (value.replace("g", "").toDouble() * 1024 * 1024).toInt()
        } else {
            value.toInt()
        }
    }
}