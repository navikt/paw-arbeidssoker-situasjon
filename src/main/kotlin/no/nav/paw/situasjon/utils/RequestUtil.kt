package no.nav.paw.situasjon.utils

import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.parseAuthorizationHeader
import no.nav.paw.situasjon.domain.Foedselsnummer
import no.nav.paw.situasjon.plugins.StatusException
import no.nav.security.token.support.v2.TokenValidationContextPrincipal

fun ApplicationCall.getClaim(issuer: String, name: String): String? =
    authentication.principal<TokenValidationContextPrincipal>()
        ?.context
        ?.getClaims(issuer)
        ?.getStringClaim(name)

fun ApplicationCall.getPidClaim(): Foedselsnummer =
    getClaim("idporten", "pid")
        ?.let { Foedselsnummer(it) }
        ?: getClaim("tokenx", "pid")
            ?.let { Foedselsnummer(it) }
        ?: throw StatusException(HttpStatusCode.Forbidden, "Fant ikke 'pid'-claim i token fra issuer")

fun ApplicationCall.getBearerToken(): String? {
    return request.parseAuthorizationHeader()?.let { authHeader ->
        if (authHeader is HttpAuthHeader.Single && authHeader.authScheme == "Bearer") {
            authHeader.blob
        } else {
            null
        }
    }
}
