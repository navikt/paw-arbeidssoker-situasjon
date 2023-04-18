package no.nav.paw.situasjon.utils

import org.slf4j.LoggerFactory

inline val <reified T : Any> T.logger get() = LoggerFactory.getLogger(T::class.java.name)
inline val secureLogger get() = LoggerFactory.getLogger("Tjenestekall")
