package no.nav.paw.situasjon.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.paw.situasjon.auth.TokenService
import no.nav.paw.situasjon.config.ServiceClientConfig
import no.nav.paw.situasjon.domain.Foedselsnummer
import no.nav.paw.situasjon.domain.RegistreringResponse
import no.nav.paw.situasjon.repository.SituasjonRepository

class SituasjonService(
    private val situasjonRepository: SituasjonRepository,
    private val tokenService: TokenService,
    private val httpClient: HttpClient,
    private val veilarbConfig: ServiceClientConfig
) {
    suspend fun hentSisteSituasjon(foedselsnummer: Foedselsnummer, token: String, callId: String): RegistreringResponse {
        return hentRegistrering(token, callId)
    }

    private suspend fun hentRegistrering(token: String, callId: String): RegistreringResponse {
        val veilarbToken = tokenService.exchangeTokenXToken(veilarbConfig.scope, token)
        return httpClient.get(veilarbConfig.url) {
            contentType(ContentType.Application.Json)
            bearerAuth(veilarbToken)
            header("Nav-Call-Id", callId)
        }.body()
    }
}
