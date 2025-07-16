package br.com.passella.jsonparser

import br.com.passella.jsonparser.exceptions.InvalidTypeException
import br.com.passella.jsonparser.exceptions.KeyNotFoundException
import br.com.passella.jsonparser.exceptions.NullValueException

class MyJson(private val sourceMap: Map<String, Any?>) {
    fun asString(key: String): String {
        return (sourceMap.takeIf { it.containsKey(key) } ?: throw KeyNotFoundException(key))
            .let { it[key] ?: throw NullValueException(key) }
            .let { it as? String ?: throw InvalidTypeException(key, "string") }
    }

    fun asDouble(key: String): Double {
        return (sourceMap.takeIf { it.containsKey(key) } ?: throw KeyNotFoundException(key))
            .let { it[key] ?: throw NullValueException(key) }
            .let { it as? Double ?: throw InvalidTypeException(key, "double") }
    }

    fun asInt(key: String): Int {
        return (sourceMap.takeIf { it.containsKey(key) } ?: throw KeyNotFoundException(key))
            .let { it[key] ?: throw NullValueException(key) }
            .let { it as? Int ?: throw InvalidTypeException(key, "int") }
    }


    override fun toString(): String {
        return buildJsonString(sourceMap)
    }

    private fun buildJsonString(map: Map<String, Any?>): String {
        val builder = StringBuilder()
        builder.append("{")

        val entries = map.entries.toList()
        entries.forEachIndexed { index, entry ->
            builder.append("\"${entry.key}\":")
            builder.append(valueToJsonString(entry.value))

            if (index < entries.size - 1) {
                builder.append(",")
            }
        }

        builder.append("}")
        return builder.toString()
    }

    private fun valueToJsonString(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"$value\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                buildJsonString(value as Map<String, Any?>)
            }

            is List<*> -> {
                val listBuilder = StringBuilder()
                listBuilder.append("[")
                value.forEachIndexed { index, item ->
                    listBuilder.append(valueToJsonString(item))
                    if (index < value.size - 1) {
                        listBuilder.append(",")
                    }
                }
                listBuilder.append("]")
                listBuilder.toString()
            }

            else -> "\"$value\""
        }
    }
}