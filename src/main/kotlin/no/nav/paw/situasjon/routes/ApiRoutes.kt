package no.nav.paw.situasjon.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.callid.callId
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import no.nav.paw.situasjon.services.SituasjonService
import no.nav.paw.situasjon.utils.getBearerToken
import no.nav.paw.situasjon.utils.getPidClaim
import no.nav.paw.situasjon.utils.logger
import org.koin.ktor.ext.inject

fun Route.apiRoutes() {
    val situasjonService: SituasjonService by inject()

    authenticate("idporten", "tokenx") {
        route("/api/v1") {
            get("/situasjon") {
                try {
                    logger.info("Henter siste situasjon for bruker")
                    val foedselsnummer = call.getPidClaim()
                    val token = call.getBearerToken()
                    val callId = call.callId
                    val registrering = situasjonService.hentSisteSituasjon(foedselsnummer, token!!, callId!!)

                    call.respond(HttpStatusCode.OK, registrering)
                } catch (error: Exception) {
                    logger.error(error.message)
                }
            }
        }
    }
}
