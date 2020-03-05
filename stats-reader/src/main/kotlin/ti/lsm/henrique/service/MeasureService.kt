package ti.lsm.henrique.service

import javax.inject.Singleton

@Singleton
class MeasureService {


    fun convertToKiloByte(value: String): Int {
        return when {
            value.contains("g") -> {
                (value.replace("g", "").toDouble() * 1024 * 1024).toInt()
            }
            value.contains("m") -> {
                (value.replace("m", "").toDouble() * 1024).toInt()
            }
            else -> {
                value.toInt()
            }
        }
    }
}