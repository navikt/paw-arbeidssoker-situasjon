package no.nav.paw.situasjon.plugins

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import no.nav.common.featuretoggle.ByClusterStrategy
import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.featuretoggle.UnleashClientImpl
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder
import no.nav.common.kafka.util.KafkaPropertiesBuilder
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.paw.situasjon.auth.TokenService
import no.nav.paw.situasjon.config.Config
import no.nav.paw.situasjon.config.NaisEnv
import no.nav.paw.situasjon.config.createDatabaseConfig
import no.nav.paw.situasjon.kafka.producers.ArbeidssokerSituasjonProducer
import no.nav.paw.situasjon.repository.SituasjonRepository
import no.nav.paw.situasjon.services.SituasjonService
import org.apache.kafka.common.serialization.StringSerializer
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDependencyInjection(config: Config) {
    install(Koin) {
        modules(
            module {
                single {
                    createDatabaseConfig(config.database.jdbcUrl)
                }

                single {
                    HttpClient {
                        install(ContentNegotiation) {
                            jackson()
                        }
                    }
                }

                single {
                    val producerProperties = when (NaisEnv.current()) {
                        NaisEnv.Local -> KafkaPropertiesBuilder.producerBuilder()
                            .withBaseProperties()
                            .withProducerId(config.kafka.producerId)
                            .withBrokerUrl(config.kafka.brokerUrl)
                            .withSerializers(StringSerializer::class.java, KafkaAvroSerializer::class.java)
                            .withProp(
                                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                                config.kafka.schemaRegisteryUrl
                            )
                            .build()

                        else -> KafkaPropertiesBuilder.producerBuilder()
                            .withProps(KafkaPropertiesPreset.aivenDefaultProducerProperties(config.kafka.producerId))
                            .withSerializers(StringSerializer::class.java, KafkaAvroSerializer::class.java)
                            .withProp(
                                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                                config.kafka.schemaRegisteryUrl
                            )
                            .withProp(SchemaRegistryClientConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO")
                            .withProp(SchemaRegistryClientConfig.USER_INFO_CONFIG, config.kafka.schemaRegisteryUserInfo)
                            .build()
                    }

                    val client = KafkaProducerClientBuilder.builder<String, String>()
                        .withProperties(producerProperties)
                        .build()
                    client
                }

                single {
                    jacksonObjectMapper().apply {
                        registerKotlinModule()
                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        registerModule(JavaTimeModule())
                    }
                }

                single<UnleashClient> {
                    UnleashClientImpl(
                        config.unleashClientConfig.url,
                        config.unleashClientConfig.appName,
                        listOf(ByClusterStrategy())
                    )
                }
                single { TokenService() }
                single { ArbeidssokerSituasjonProducer(get(), config.kafka.producers.arbeidssokerSituasjon.topic) }
                single { SituasjonRepository(get()) }
                single { SituasjonService(get(), get(), get(), config.veilarbregistreringClientConfig) }
            }
        )
    }
}
