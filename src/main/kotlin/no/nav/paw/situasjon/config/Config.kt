package no.nav.paw.situasjon.config

import io.github.cdimascio.dotenv.dotenv
import no.nav.security.token.support.v2.RequiredClaims

val dotenv = dotenv { ignoreIfMissing = true }

data class Config(
    val database: DatabaseConfig = DatabaseConfig(
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_SITUASJON_SITUASJON_HOST"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_SITUASJON_SITUASJON_PORT"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_SITUASJON_SITUASJON_DATABASE"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_SITUASJON_SITUASJON_USERNAME"],
        dotenv["NAIS_DATABASE_PAW_ARBEIDSSOKER_SITUASJON_SITUASJON_PASSWORD"]
    ),
    val naisEnv: NaisEnv = NaisEnv.current(),
    val unleashClientConfig: UnleashClientConfig = UnleashClientConfig(
        dotenv["UNLEASH_URL"],
        dotenv["NAIS_APP_NAME"]
    ),
    val authentication: List<AuthProvider> = listOf(
        AuthProvider(
            name = "idporten",
            discoveryUrl = dotenv["IDPORTEN_WELL_KNOWN_URL"],
            acceptedAudience = listOf(dotenv["IDPORTEN_CLIENT_ID"]),
            cookieName = "selvbetjening-idtoken",
            requiredClaims = RequiredClaims("idporten", arrayOf("pid", "acr"))
        ),
        AuthProvider(
            name = "tokenx",
            discoveryUrl = dotenv["TOKEN_X_WELL_KNOWN_URL"],
            acceptedAudience = listOf(dotenv["TOKEN_X_CLIENT_ID"]),
            requiredClaims = RequiredClaims(dotenv["TOKEN_X_ISSUER"], arrayOf("pid"))
        )
    ),
    val kafka: KafkaConfig = KafkaConfig(
        dotenv["KAFKA_BROKER_URL"],
        dotenv["KAFKA_PRODUCER_ID"],
        dotenv["KAFKA_SCHEMA_REGISTRY"],
        "${dotenv["KAFKA_SCHEMA_REGISTRY_USER"]}:${dotenv["KAFKA_SCHEMA_REGISTRY_PASSWORD"]}",
        KafkaProducers(
            KafkaProducer(
                dotenv["KAFKA_PRODUCER_ARBEIDSSOKER_SITUASJON_TOPIC"]
            )
        )
    ),
    val veilarbregistreringClientConfig: ServiceClientConfig = ServiceClientConfig(
        dotenv["VEILARBREGISTRERING_URL"],
        dotenv["VEILARBREGISTRERING_SCOPE"]
    )
)

data class DatabaseConfig(
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String
) {
    val jdbcUrl: String get() = "jdbc:postgresql://$host:$port/$database?user=$username&password=$password"
}

data class KafkaConfig(
    val brokerUrl: String? = null,
    val producerId: String,
    val schemaRegisteryUrl: String?,
    val schemaRegisteryUserInfo: String?,
    val producers: KafkaProducers
)

data class KafkaProducers(
    val arbeidssokerSituasjon: KafkaProducer
)

data class KafkaProducer(
    val topic: String
)

data class ServiceClientConfig(
    val url: String,
    val scope: String
)

data class UnleashClientConfig(
    val url: String,
    val appName: String
)

data class AuthProvider(
    val name: String,
    val discoveryUrl: String,
    val acceptedAudience: List<String>,
    val cookieName: String? = null,
    val requiredClaims: RequiredClaims? = null
)

enum class NaisEnv(val clusterName: String) {
    Local("local"),
    DevGCP("dev-gcp"),
    ProdGCP("prod-gcp");

    companion object {
        fun current(): NaisEnv = when (System.getenv("NAIS_CLUSTER_NAME")) {
            DevGCP.clusterName -> DevGCP
            ProdGCP.clusterName -> ProdGCP
            else -> Local
        }
    }

    fun isLocal(): Boolean = this === Local
    fun isDevGCP(): Boolean = this === DevGCP
    fun isProdGCP(): Boolean = this === ProdGCP
}
