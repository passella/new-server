package br.com.passella.payments.adapter.input.http.handler

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.Handler
import br.com.passella.httpserver.core.HttpResponseWriter
import br.com.passella.httpserver.core.HttpStatusCode
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.jsonparser.MyJsonParser
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.time.Duration
import java.net.http.HttpRequest as JavaHttpRequest

class PostPaymentsHandler(
    private val responseWriter: HttpResponseWriter,
    private val jsonParser: MyJsonParser
) : Handler {
    companion object {
        private val logger = FastLogger.getLogger(PostPaymentsHandler::class.java)
        private val httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()
    }

    override fun handle(request: HttpRequest) {
        logger.info { "Processando requisição de pagamento: ${request.body}" }

        val paymentData = jsonParser.parse(request.body)
        val correlationId = paymentData.asString("correlationId")
        val amount = paymentData.asInt("amount")
        val requestedAt = paymentData.asString("requestedAt")
        logger.info {
            "Dados do pagamento: correlationId=$correlationId, amount=$amount, requestedAt=$requestedAt"
        }

        try {
            /*val jsonPayload = """
                {
                    "correlationId": "$correlationId",
                    "amount": $amount,
                    "requestedAt": "$requestedAt"
                }
            """.trimIndent()

            val httpRequest = JavaHttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8001/payments"))
                .header("Content-Type", "application/json")
                .POST(JavaHttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(10))
                .build()

            val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())
            val responseCode = response.statusCode()
            val responseBody = response.body()

            logger.info { "Resposta do serviço de pagamentos: código=$responseCode, corpo=$responseBody" }*/

            responseWriter.writeResponse(
                request.socket,
                request.body,
                HttpStatusCode.OK
            )
        } catch (e: IOException) {
            logger.error(e) { "Erro de I/O ao processar pagamento" }
            responseWriter.writeResponse(
                request.socket,
                "Erro de comunicação com o serviço de pagamentos: ${e.message}",
                HttpStatusCode.INTERNAL_SERVER_ERROR
            )
        } catch (e: InterruptedException) {
            logger.error(e) { "Operação de pagamento interrompida" }
            Thread.currentThread().interrupt()
            responseWriter.writeResponse(
                request.socket,
                "Processamento de pagamento interrompido",
                HttpStatusCode.INTERNAL_SERVER_ERROR
            )
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Erro de argumento inválido ao processar pagamento" }
            responseWriter.writeResponse(
                request.socket,
                "Configuração inválida para o serviço de pagamentos: ${e.message}",
                HttpStatusCode.INTERNAL_SERVER_ERROR
            )
        }
    }
}