package no.nav.paw.situasjon.auth

import no.nav.common.token_client.builder.TokenXTokenClientBuilder
import no.nav.paw.situasjon.utils.logger

class TokenService {
    fun exchangeTokenXToken(scope: String, token: String): String {
        logger.info("Veksler TokenX-token mot $scope")
        return tokendingsClient.exchangeOnBehalfOfToken(scope, token)
    }

    private val tokendingsClient = TokenXTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildOnBehalfOfTokenClient()
}
