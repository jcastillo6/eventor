package com.jcastillo6.eventor

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.kafka.requestreply.RequestReplyFuture
import java.time.Duration
import java.util.concurrent.TimeUnit


@SpringBootApplication
class EventorApplication {
    @Bean
    fun topic() = NewTopic("topic1", 10, 1)

    @KafkaListener(id = "myId", topics = ["topic1"])
    fun listen(value: String?) {
        println(value)
    }

    @Bean
    fun runner(template: KafkaTemplate<Int?, String?>) =
        ApplicationRunner {
            val future = template.send("topic1", 1, "test")
            future.whenComplete { result, _ -> println(result) }
        }

    @Bean
    fun runnerReply(replyingKafkaTemplate: ReplyingKafkaTemplate<String?, String?, String?>) =
        ApplicationRunner {
            if (!replyingKafkaTemplate.waitForAssignment(Duration.ofSeconds(10))) {
                throw IllegalStateException("Reply container did not initialize")
            }
            val record = ProducerRecord<String?, String?>("kRequests", "foo")
            val replyFuture: RequestReplyFuture<String?, String?, String?> = replyingKafkaTemplate.sendAndReceive(record)
            val sendResult = replyFuture.sendFuture[10, TimeUnit.SECONDS]
            println("Sent ok: " + sendResult.recordMetadata)
            val consumerRecord = replyFuture[10, TimeUnit.SECONDS]
            println("Return value: " + consumerRecord.value())
        }
}



fun main(args: Array<String>) {
    runApplication<EventorApplication>(*args)
}
