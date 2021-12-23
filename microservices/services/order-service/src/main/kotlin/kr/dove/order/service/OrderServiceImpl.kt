package kr.dove.order.service

import kr.dove.api.core.order.Order
import kr.dove.api.core.order.OrderService
import kr.dove.api.exceptions.OperationFailedException
import kr.dove.order.persistence.OrderDocument
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType
import org.elasticsearch.search.sort.ScriptSortBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class OrderServiceImpl(
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchOperations
): OrderService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun save(order: Order): Mono<Order> {
        return reactiveElasticsearchTemplate
            .save(OrderDocument(
                UUID.randomUUID().toString(),
                order
            ))
            .flatMap { doc ->
                Mono.just(order.apply {
                    this.orderId = doc.docId
                })
            }
            .onErrorResume {
                val errorMsg = "Error occurred while saving entity. ${order.transactionId}"
                logger.error(errorMsg)
                Mono.error(OperationFailedException(errorMsg))
            }
    }

    override fun list(productId: Long?, memberId: Long?): Flux<Order> {
        val queryBuilder: BoolQueryBuilder = QueryBuilders.boolQuery()
        productId ?. let { queryBuilder.must(QueryBuilders.matchQuery("order.cart.product.productId", it)) }
        memberId ?. let { queryBuilder.must(QueryBuilders.matchQuery("order.member.memberId", it)) }
        return reactiveElasticsearchTemplate
            .search(NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                    .boolQuery()
                    .must(queryBuilder))
                //  docs registered recently would have the highest order.
                .withSort(SortBuilders
                    .scriptSort(Script(
                        ScriptType.INLINE, "painless", """
                                                long current = params['current'];
                                                long diff = current - doc['created'].value;
                                                return diff
                        """.trimIndent(), mutableMapOf(Pair<String, Any>("current", Date().time))
                    ), ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC)
                )
                .build(), OrderDocument::class.java)
            .flatMap { hit ->
                Mono.just(hit.content.order.apply {
                    this.orderId = hit.content.docId
                })
            }
            .onErrorResume {
                val errorMsg = "Error occurred while retrieving entity."
                logger.error(errorMsg)
                Flux.error(OperationFailedException(errorMsg))
            }
    }
}