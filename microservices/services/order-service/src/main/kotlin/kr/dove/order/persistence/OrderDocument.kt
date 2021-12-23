package kr.dove.order.persistence

import kr.dove.api.core.order.Order
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Mapping
import org.springframework.data.elasticsearch.annotations.Setting

@Document(
    indexName = "order"
)
@Setting(settingPath = "/elasticsearch/settings/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/mappings.json")
data class OrderDocument(
    @Id val docId: String,
    val order: Order
)
