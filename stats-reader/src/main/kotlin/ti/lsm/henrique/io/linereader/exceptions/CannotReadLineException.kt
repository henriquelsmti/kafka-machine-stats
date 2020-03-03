package ti.lsm.henrique.io.linereader.exceptions

class CannotReadLineException(line: String) : RuntimeException("Cannot read the line: $line")