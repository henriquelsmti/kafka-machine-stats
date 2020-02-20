package ti.lsm.henrique.io.linereader.exceptions

import java.lang.RuntimeException

class LineReaderException(msg: String, e: Exception? = null) : RuntimeException(msg, e)