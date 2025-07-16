package br.com.passella.jsonparser

import br.com.passella.jsonparser.exceptions.InvalidJsonException


class MyJsonParser {
    private var currentPosition = 0
    private lateinit var json: String
    private val jsonLength: Int
        get() = json.length

    fun parse(input: String): MyJson {
        json = input
        currentPosition = 0
        val result = parseValue()
        skipWhitespace()
        if (currentPosition < jsonLength) {
            throw InvalidJsonException("Unexpected character at position $currentPosition: '${json[currentPosition]}'")
        }
        return when (result) {
            is Map<*, *> -> MyJson(result as Map<String, Any?>)
            else -> throw InvalidJsonException("Root element must be an object")
        }
    }

    private fun parseValue(): Any? {
        skipWhitespace()

        return when {
            currentPosition >= jsonLength -> throw InvalidJsonException(UNEXPECTED_END_OF_INPUT)
            json[currentPosition] == OBJECT_START -> parseObject()
            json[currentPosition] == ARRAY_START -> parseArray()
            json[currentPosition] == QUOTE -> parseString()
            json[currentPosition] == TRUE_START -> parseTrue()
            json[currentPosition] == FALSE_START -> parseFalse()
            json[currentPosition] == NULL_START -> parseNull()
            json[currentPosition] == MINUS || json[currentPosition] in DIGIT_0..DIGIT_9 -> parseNumber()
            else -> throw InvalidJsonException(
                "Unexpected character at position $currentPosition: '${json[currentPosition]}'"
            )
        }
    }

    private fun parseObject(): Map<String, Any?> {
        currentPosition++
        val result = mutableMapOf<String, Any?>()
        skipWhitespace()

        if (currentPosition < jsonLength && json[currentPosition] == OBJECT_END) {
            currentPosition++
            return result
        }

        while (true) {
            skipWhitespace()

            if (json[currentPosition] != QUOTE) {
                throw InvalidJsonException(
                    "Expected string key at position $currentPosition, got '${json[currentPosition]}'"
                )
            }
            val key = parseString()
            skipWhitespace()
            if (currentPosition >= jsonLength || json[currentPosition] != COLON) {
                throw InvalidJsonException("Expected ':' at position $currentPosition")
            }
            currentPosition++

            val value = parseValue()
            result[key] = value

            skipWhitespace()

            if (currentPosition >= jsonLength) {
                throw InvalidJsonException(UNEXPECTED_END_OF_INPUT)
            }

            if (json[currentPosition] == OBJECT_END) {
                currentPosition++
                return result
            }

            if (json[currentPosition] != COMMA) {
                throw InvalidJsonException(
                    "Expected ',' or '}' at position $currentPosition, got '${json[currentPosition]}'"
                )
            }
            currentPosition++
        }
    }

    private fun parseArray(): List<Any?> {
        currentPosition++
        val result = mutableListOf<Any?>()
        skipWhitespace()

        if (currentPosition < jsonLength && json[currentPosition] == ARRAY_END) {
            currentPosition++
            return result
        }

        while (true) {
            val value = parseValue()
            result.add(value)

            skipWhitespace()

            if (currentPosition >= jsonLength) {
                throw InvalidJsonException(UNEXPECTED_END_OF_INPUT)
            }

            if (json[currentPosition] == ARRAY_END) {
                currentPosition++
                return result
            }

            if (json[currentPosition] != COMMA) {
                throw InvalidJsonException(
                    "Expected ',' or ']' at position $currentPosition, got '${json[currentPosition]}'"
                )
            }
            currentPosition++
        }
    }

    private fun parseString(): String {
        currentPosition++
        val start = currentPosition
        val sb = StringBuilder()

        while (currentPosition < jsonLength) {
            val c = json[currentPosition++]

            if (c == QUOTE) {
                return sb.toString()
            }

            if (c == ESCAPE) {
                if (currentPosition >= jsonLength) {
                    throw InvalidJsonException("Unexpected end of input in escape sequence")
                }

                when (val escapeChar = json[currentPosition++]) {
                    QUOTE, ESCAPE, FORWARD_SLASH -> sb.append(escapeChar)
                    ESCAPE_B -> sb.append(BACKSPACE)
                    ESCAPE_F -> sb.append(FORM_FEED)
                    ESCAPE_N -> sb.append(NEWLINE)
                    ESCAPE_R -> sb.append(CARRIAGE_RETURN)
                    ESCAPE_T -> sb.append(TAB)
                    ESCAPE_U -> {
                        if (currentPosition + HEX_UNICODE_LENGTH > jsonLength) {
                            throw InvalidJsonException("Unexpected end of input in unicode escape")
                        }
                        val hex = json.substring(currentPosition, currentPosition + HEX_UNICODE_LENGTH)
                        currentPosition += HEX_UNICODE_LENGTH
                        try {
                            sb.append(hex.toInt(HEX_RADIX).toChar())
                        } catch (_: NumberFormatException) {
                            throw InvalidJsonException("Invalid unicode escape sequence: \\u$hex")
                        }
                    }

                    else -> throw InvalidJsonException("Invalid escape sequence: \\$escapeChar")
                }
            } else {
                sb.append(c)
            }
        }

        throw InvalidJsonException("Unterminated string starting at position $start")
    }

    private fun parseNumber(): Number {
        val start = currentPosition
        var isDouble = false

        if (json[currentPosition] == MINUS) {
            currentPosition++
        }

        if (currentPosition < jsonLength && json[currentPosition] == DIGIT_0) {
            currentPosition++
        } else if (currentPosition < jsonLength && json[currentPosition] in DIGIT_1..DIGIT_9) {
            currentPosition++
            while (currentPosition < jsonLength && json[currentPosition] in DIGIT_0..DIGIT_9) {
                currentPosition++
            }
        } else {
            throw InvalidJsonException("Invalid number at position $start")
        }

        if (currentPosition < jsonLength && json[currentPosition] == DECIMAL_POINT) {
            isDouble = true
            currentPosition++
            if (currentPosition >= jsonLength || json[currentPosition] !in DIGIT_0..DIGIT_9) {
                throw InvalidJsonException("Expected digit after decimal point at position $currentPosition")
            }
            while (currentPosition < jsonLength && json[currentPosition] in DIGIT_0..DIGIT_9) {
                currentPosition++
            }
        }

        if ((currentPosition < jsonLength)
            && (json[currentPosition] == EXPONENT_LOWER || json[currentPosition] == EXPONENT_UPPER)) {
            isDouble = true
            currentPosition++
            if (currentPosition < jsonLength && (json[currentPosition] == PLUS || json[currentPosition] == MINUS)) {
                currentPosition++
            }
            if (currentPosition >= jsonLength || json[currentPosition] !in DIGIT_0..DIGIT_9) {
                throw InvalidJsonException("Expected digit in exponent at position $currentPosition")
            }
            while (currentPosition < jsonLength && json[currentPosition] in DIGIT_0..DIGIT_9) {
                currentPosition++
            }
        }

        val numStr = json.substring(start, currentPosition)
        return if (isDouble) {
            numStr.toDouble()
        } else {
            try {
                numStr.toInt()
            } catch (_: NumberFormatException) {
                try {
                    numStr.toLong()
                } catch (_: NumberFormatException) {
                    numStr.toDouble()
                }
            }
        }
    }

    private fun parseTrue(): Boolean {
        if (currentPosition + TRUE_LENGTH <= jsonLength && json.substring(
                currentPosition,
                currentPosition + TRUE_LENGTH
            ) == TRUE_LITERAL
        ) {
            currentPosition += TRUE_LENGTH
            return true
        }
        throw InvalidJsonException("Expected 'true' at position $currentPosition")
    }

    private fun parseFalse(): Boolean {
        if (currentPosition + FALSE_LENGTH <= jsonLength && json.substring(
                currentPosition,
                currentPosition + FALSE_LENGTH
            ) == FALSE_LITERAL
        ) {
            currentPosition += FALSE_LENGTH
            return false
        }
        throw InvalidJsonException("Expected 'false' at position $currentPosition")
    }

    private fun parseNull(): Any? {
        if (currentPosition + NULL_LENGTH <= jsonLength && json.substring(
                currentPosition,
                currentPosition + NULL_LENGTH
            ) == NULL_LITERAL
        ) {
            currentPosition += NULL_LENGTH
            return null
        }
        throw InvalidJsonException("Expected 'null' at position $currentPosition")
    }

    private fun skipWhitespace() {
        while (currentPosition < jsonLength && json[currentPosition].isWhitespace()) {
            currentPosition++
        }
    }

    companion object {

        private const val OBJECT_START = '{'
        private const val OBJECT_END = '}'
        private const val ARRAY_START = '['
        private const val ARRAY_END = ']'
        private const val QUOTE = '"'
        private const val COLON = ':'
        private const val COMMA = ','
        private const val ESCAPE = '\\'
        private const val FORWARD_SLASH = '/'
        private const val MINUS = '-'
        private const val PLUS = '+'
        private const val DECIMAL_POINT = '.'
        private const val EXPONENT_LOWER = 'e'
        private const val EXPONENT_UPPER = 'E'
        private const val DIGIT_0 = '0'
        private const val DIGIT_1 = '1'
        private const val DIGIT_9 = '9'
        private const val TRUE_START = 't'
        private const val FALSE_START = 'f'
        private const val NULL_START = 'n'
        private const val ESCAPE_B = 'b'
        private const val ESCAPE_F = 'f'
        private const val ESCAPE_N = 'n'
        private const val ESCAPE_R = 'r'
        private const val ESCAPE_T = 't'
        private const val ESCAPE_U = 'u'
        private const val BACKSPACE = '\b'
        private const val FORM_FEED = '\u000C'
        private const val NEWLINE = '\n'
        private const val CARRIAGE_RETURN = '\r'
        private const val TAB = '\t'
        private const val TRUE_LITERAL = "true"
        private const val FALSE_LITERAL = "false"
        private const val NULL_LITERAL = "null"
        private const val TRUE_LENGTH = 4
        private const val FALSE_LENGTH = 5
        private const val NULL_LENGTH = 4
        private const val HEX_UNICODE_LENGTH = 4
        private const val HEX_RADIX = 16
        private const val UNEXPECTED_END_OF_INPUT = "Unexpected end of input"
    }
}