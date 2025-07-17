package br.com.passella.payments.handers

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.HttpHandler
import br.com.passella.httpserver.HttpRequest
import br.com.passella.httpserver.HttpResponse

class PaymentsHandler : HttpHandler {
    override fun handle(
        request: HttpRequest,
        response: HttpResponse,
    ) {
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
