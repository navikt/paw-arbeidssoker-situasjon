package no.nav.paw.situasjon.kafka.producers

import no.nav.common.kafka.producer.KafkaProducerClient
import no.nav.paw.situasjon.ArbeidssokerSituasjonEvent
import no.nav.paw.situasjon.utils.CallId.callId
import no.nav.paw.situasjon.utils.logger
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import java.nio.charset.StandardCharsets
import java.util.*

class ArbeidssokerSituasjonProducer(
    private val kafkaProducerClient: KafkaProducerClient<String, ArbeidssokerSituasjonEvent>,
    private val topic: String
) {
    fun publish(value: ArbeidssokerSituasjonEvent) {
        val record: ProducerRecord<String, ArbeidssokerSituasjonEvent> = ProducerRecord(
            topic,
            null,
            UUID.randomUUID().toString(),
            value,
            listOf(RecordHeader("CallId", callId.toByteArray(StandardCharsets.UTF_8)))
        )

        kafkaProducerClient.sendSync(record)
        logger.info("Sendte melding om situasjonsendring til $topic")
    }
}
