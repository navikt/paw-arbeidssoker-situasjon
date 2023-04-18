package no.nav.paw.situasjon

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import no.nav.paw.situasjon.config.Config
import no.nav.paw.situasjon.config.migrateDatabase
import no.nav.paw.situasjon.plugins.configureAuthentication
import no.nav.paw.situasjon.plugins.configureDependencyInjection
import no.nav.paw.situasjon.plugins.configureHTTP
import no.nav.paw.situasjon.plugins.configureLogging
import no.nav.paw.situasjon.plugins.configureSerialization
import no.nav.paw.situasjon.routes.apiRoutes
import no.nav.paw.situasjon.routes.internalRoutes
import no.nav.paw.situasjon.routes.swaggerRoutes
import org.koin.ktor.ext.inject
import javax.sql.DataSource

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val config = Config()

    // Plugins
    configureDependencyInjection(config)
    configureAuthentication(config.authentication)
    configureHTTP()
    configureLogging()
    configureSerialization()

    // Migrate database
    val dataSource by inject<DataSource>()
    migrateDatabase(dataSource)

    // Routes
    routing {
        internalRoutes()
        swaggerRoutes()
        apiRoutes()
    }
}
