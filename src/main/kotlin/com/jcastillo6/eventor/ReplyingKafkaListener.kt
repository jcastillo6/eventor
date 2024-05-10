package com.jcastillo6.eventor

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo
import java.util.*


@Configuration
class ReplyingKafkaListener {

    @KafkaListener(id = "server", topics = ["kRequests"], containerFactory = "kafkaListenerContainerFactory")
    @SendTo // use default replyTo expression
    fun listen(value: String): String {
        println("Server received: $value")
        return value.uppercase(Locale.getDefault())
    }

}
