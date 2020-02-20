package ti.lsm.henrique.io.exceptions

import java.lang.RuntimeException

class IOException(msg: String, e: Exception? = null) : RuntimeException(msg, e)