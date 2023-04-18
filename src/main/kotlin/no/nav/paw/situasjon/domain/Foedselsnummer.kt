package no.nav.paw.situasjon.domain

data class Foedselsnummer(val foedselsnummer: String) {
    override fun toString(): String = "*".repeat(11)
}
