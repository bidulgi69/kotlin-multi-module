package kr.dove.order

import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import reactor.core.publisher.Mono

@ConfigurationProperties(prefix = "elastic.index")
@SpringBootApplication(scanBasePackages = ["kr.dove"])
class OrderServiceApplication(
	private val reactiveElasticsearchClient: ReactiveElasticsearchClient
) {
	private val logger: Logger = LoggerFactory.getLogger(this::class.java)
	lateinit var name: String
	lateinit var shards: String
	lateinit var replicas: String

	@Bean
	fun createIndex() {
		reactiveElasticsearchClient
			.indices()
			.getIndex(GetIndexRequest(name))
			.onErrorResume {
				logger.info("There is no index named $name.")
				Mono.empty()
			} //  if index does not exist, move to switchIfEmpty function.
			.flatMap {
				logger.info("Index $name already exists.")
				Mono.just(true)
			}
			.switchIfEmpty(Mono.fromCallable {
				val createIndexRequest = CreateIndexRequest(name)
				createIndexRequest.settings(
					Settings
						.builder()
						.put("index.number_of_shards", shards)
						.put("index.number_of_replicas", replicas)
				)
				createIndexRequest.source("{\n" +
						"  \"settings\": {\n" +
						"    \"max_ngram_diff\": \"5\",\n" +
						"    \"analysis\": {\n" +
						"      \"analyzer\": {\n" +
						"        \"n_gram_analyzer\": {\n" +
						"          \"tokenizer\": \"text_tokenizer\",\n" +
						"          \"filter\": [\n" +
						"            \"lowercase\"\n" +
						"          ]\n" +
						"        }\n" +
						"      },\n" +
						"      \"tokenizer\": {\n" +
						"        \"text_tokenizer\": {\n" +
						"          \"type\": \"ngram\",\n" +
						"          \"min_gram\": 2,\n" +
						"          \"max_gram\": 7,\n" +
						"          \"token_chars\": [\n" +
						"            \"letter\",\n" +
						"            \"digit\",\n" +
						"            \"symbol\",\n" +
						"            \"punctuation\"\n" +
						"          ]\n" +
						"        }\n" +
						"      }\n" +
						"    }\n" +
						"  }, \n" +
						"  \"mappings\": {\n" +
						"    \"properties\": {\n" +
						"      \"order\": {\n" +
						"        \"properties\": {\n" +
						"          \"transactionId\": {\n" +
						"            \"type\": \"keyword\"\n" +
						"          },\n" +
						"          \"orderId\": {\n" +
						"            \"type\": \"keyword\"\n" +
						"          },\n" +
						"          \"identification\": {\n" +
						"            \"type\": \"keyword\"\n" +
						"          },\n" +
						"          \"created\": {\n" +
						"            \"type\": \"long\"\n" +
						"          },\n" +
						"          \"shop\": {\n" +
						"            \"properties\": {\n" +
						"              \"shopId\": {\n" +
						"                \"type\": \"long\"\n" +
						"              },\n" +
						"              \"name\": {\n" +
						"                \"type\": \"text\",\n" +
						"                \"analyzer\": \"n_gram_analyzer\"\n" +
						"              },\n" +
						"              \"location\": {\n" +
						"                \"type\": \"text\"\n" +
						"              },\n" +
						"              \"version\": {\n" +
						"                \"type\": \"long\"\n" +
						"              }\n" +
						"            }\n" +
						"          },\n" +
						"          \"member\": {\n" +
						"            \"properties\": {\n" +
						"              \"memberId\": {\n" +
						"                \"type\": \"long\"\n" +
						"              },\n" +
						"              \"name\": {\n" +
						"                \"type\": \"text\",\n" +
						"                \"analyzer\": \"n_gram_analyzer\"\n" +
						"              },\n" +
						"              \"id\": {\n" +
						"                \"type\": \"keyword\"\n" +
						"              },\n" +
						"              \"version\": {\n" +
						"                \"type\": \"long\"\n" +
						"              }\n" +
						"            }\n" +
						"          },\n" +
						"          \"cart\": {\n" +
						"            \"properties\": {\n" +
						"              \"cartId\": {\n" +
						"                \"type\": \"long\"\n" +
						"              },\n" +
						"              \"product\": {\n" +
						"                \"properties\": {\n" +
						"                  \"productId\": {\n" +
						"                    \"type\": \"long\"\n" +
						"                  },\n" +
						"                  \"name\": {\n" +
						"                    \"type\": \"text\",\n" +
						"                    \"analyzer\": \"n_gram_analyzer\"\n" +
						"                  },\n" +
						"                  \"code\": {\n" +
						"                    \"type\": \"keyword\"\n" +
						"                  },\n" +
						"                  \"price\": {\n" +
						"                    \"type\": \"integer\"\n" +
						"                  },\n" +
						"                  \"version\": {\n" +
						"                    \"type\": \"long\"\n" +
						"                  }\n" +
						"                }\n" +
						"              },\n" +
						"              \"count\": {\n" +
						"                \"type\": \"integer\"\n" +
						"              },\n" +
						"              \"version\": {\n" +
						"                \"type\": \"long\"\n" +
						"              }\n" +
						"            }\n" +
						"          },\n" +
						"          \"version\": {\n" +
						"            \"type\": \"long\"\n" +
						"          }\n" +
						"        }\n" +
						"      }\n" +
						"    }\n" +
						"  }\n" +
						"}\n", XContentType.JSON)
				reactiveElasticsearchClient
					.indices()
					.createIndex(createIndexRequest)
					.subscribe {
						logger.info("CreateIndexRequest($name) will be executed.")
					}
				true
			}).subscribe { println("Check Index($name) on start executed.") }
	}
}

fun main(args: Array<String>) {
	runApplication<OrderServiceApplication>(*args)
}
