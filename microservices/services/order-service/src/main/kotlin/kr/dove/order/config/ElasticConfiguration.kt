package kr.dove.order.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@ConfigurationProperties(prefix = "elastic")
@EnableElasticsearchRepositories(basePackages = ["kr.dove.order.persistence"])
@ComponentScan(basePackages = ["kr.dove.order"])
class ElasticConfiguration(): AbstractReactiveElasticsearchConfiguration() {

    lateinit var host: String
    lateinit var port: String

    @Bean
    override fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val clientConfiguration: ClientConfiguration =
            ClientConfiguration
                .builder()
                .connectedTo("$host:$port")
                .build()
        return ReactiveRestClients.create(clientConfiguration)
    }

    @Bean
    fun reactiveElasticsearchTemplate(): ReactiveElasticsearchOperations {
        return ReactiveElasticsearchTemplate(reactiveElasticsearchClient())
    }
}