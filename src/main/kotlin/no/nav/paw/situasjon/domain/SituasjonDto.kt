package no.nav.paw.situasjon.domain

import java.time.LocalDateTime

data class SituasjonDto(
    val id: Int,
    val opprettet: LocalDateTime,
    val endret: LocalDateTime
)
