package ti.lsm.henrique.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class LoadAverage(val lastMinute: Double, val lastFiveMinutes:Double, val lastFifteenMinutes:Double)