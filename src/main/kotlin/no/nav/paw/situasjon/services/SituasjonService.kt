package no.nav.paw.situasjon.services

import no.nav.paw.situasjon.domain.Foedselsnummer
import no.nav.paw.situasjon.domain.SituasjonDto
import no.nav.paw.situasjon.repository.SituasjonRepository

class SituasjonService(
    private val situasjonRepository: SituasjonRepository
) {
    fun hentSisteSituasjon(foedselsnummer: Foedselsnummer): SituasjonDto? {
        return situasjonRepository.hentSiste(foedselsnummer)
    }
}
