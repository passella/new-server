package br.com.passella.payments.hander

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse
import br.com.passella.jsonparser.MyJsonParser

class PaymentsHandler(
    private val jsonParser: MyJsonParser,
) : HttpHandler {
    override fun handle(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        val json = jsonParser.parse(request.body)
        val correlationId = json.asString("correlationId")
        val amount = json.asInt("amount")
        val requestedAt = json.asString("requestedAt")

        logger.info { "CorrelationId: $correlationId, Amount: $amount, RequestedAt: $requestedAt" }

        logger.info { "Processando pagamento: ${request.body}" }
        response
            .status(200)
            .header("Content-Type", "application/json")
            .body("""{"status":"ACCEPTED","id":"payment-123"}""")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(PaymentsHandler::class.java)
    }
}
