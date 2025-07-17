package br.com.passella.payments.hander

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse

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
