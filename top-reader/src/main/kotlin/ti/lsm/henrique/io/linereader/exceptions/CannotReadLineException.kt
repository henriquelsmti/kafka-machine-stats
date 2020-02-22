package ti.lsm.henrique.io.linereader.exceptions

import java.lang.RuntimeException

class CannotReadLineException(line: String) : RuntimeException("Cannot read the line: $line")