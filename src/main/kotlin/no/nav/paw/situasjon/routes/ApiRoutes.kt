package no.nav.paw.situasjon.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import no.nav.paw.situasjon.utils.getPidClaim
import no.nav.paw.situasjon.utils.logger

fun Route.apiRoutes() {
    authenticate("idporten", "tokenx") {
        route("/api/v1") {
            get("/situasjon") {
                try {
                    logger.info("Henter siste situasjon for bruker")
                    val foedselsnummer = call.getPidClaim()

                    call.respond(HttpStatusCode.OK, "OK")
                } catch (error: Exception) {
                    logger.error(error.message)
                }
            }
        }
    }
}
