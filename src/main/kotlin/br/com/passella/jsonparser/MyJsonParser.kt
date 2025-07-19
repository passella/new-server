package br.com.passella.jsonparser

import br.com.passella.jsonparser.exceptions.InvalidJsonException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import java.io.IOException

class MyJsonParser {
    private val objectMapper = ObjectMapper()

    init {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    @Suppress("UNCHECKED_CAST")
    fun parse(input: String): MyJson {
        try {
            val result = objectMapper.readValue(input, Map::class.java)
            return MyJson(result as Map<String, Any?>)
        } catch (e: MismatchedInputException) {
            throw InvalidJsonException("Formato JSON inválido: ${e.message ?: "Erro desconhecido"}", cause = e)
        } catch (e: UnrecognizedPropertyException) {
            throw InvalidJsonException(
                "Propriedade não reconhecida no JSON: " +
                    "${e.message ?: "Erro desconhecido"}",
                cause = e,
            )
        } catch (e: IOException) {
            throw InvalidJsonException("Erro ao processar JSON: ${e.message ?: "Erro desconhecido"}", cause = e)
        }
    }
}
