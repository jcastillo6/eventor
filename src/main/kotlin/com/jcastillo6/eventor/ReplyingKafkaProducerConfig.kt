package com.jcastillo6.eventor

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate

@Configuration
class ReplyingKafkaProducerConfig {

    @Bean
    fun producerReplyingFactory() = DefaultKafkaProducerFactory<String, String>(producerConfigs())

    fun producerConfigs(): Map<String, Any> =
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "1"
        )

    @Bean
    fun replyingTemplate(
        repliesContainer: ConcurrentMessageListenerContainer<String, String>?
    ): ReplyingKafkaTemplate<String?, String?, String?> {
        return ReplyingKafkaTemplate(producerReplyingFactory(), repliesContainer)
    }

    @Bean
    fun repliesContainer(
        containerFactory: ConcurrentKafkaListenerContainerFactory<String, String>
    ): ConcurrentMessageListenerContainer<String, String> {
        val repliesContainer =
            containerFactory.createContainer("kReplies")
        repliesContainer.containerProperties.setGroupId("repliesGroup")
        repliesContainer.isAutoStartup = false
        return repliesContainer
    }

    @Bean
    fun kRequests(): NewTopic {
        return TopicBuilder.name("kRequests")
            .partitions(10)
            .replicas(2)
            .build()
    }

    @Bean
    fun kReplies(): NewTopic {
        return TopicBuilder.name("kReplies")
            .partitions(10)
            .replicas(2)
            .build()
    }
}