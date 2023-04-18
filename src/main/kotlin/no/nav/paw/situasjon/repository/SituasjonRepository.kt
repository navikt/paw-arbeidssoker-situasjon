package no.nav.paw.situasjon.repository

import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.paw.situasjon.domain.Foedselsnummer
import no.nav.paw.situasjon.domain.SituasjonDto
import javax.sql.DataSource

class SituasjonRepository(private val dataSource: DataSource) {
    fun hentSiste(foedselsnummer: Foedselsnummer): SituasjonDto? {
        sessionOf(dataSource).use { session ->
            val query =
                queryOf(
                    "SELECT * FROM $SITUASJON_TABELL WHERE foedselsnummer = ? ORDER BY endret DESC LIMIT 1",
                    foedselsnummer.foedselsnummer
                ).map { it.tilSituasjon() }.asSingle
            return session.run(query)
        }
    }

    private fun Row.tilSituasjon() = SituasjonDto(
        int("id"),
        localDateTime("opprettet"),
        localDateTime("endret")
    )

    companion object {
        const val SITUASJON_TABELL = "profilering"
    }
}
