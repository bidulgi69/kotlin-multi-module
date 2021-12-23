package kr.dove.api.core.product

import com.fasterxml.jackson.annotation.JsonProperty

data class Product(
    @field:JsonProperty("product_id") var productId: Long? = null,
    @field:JsonProperty("name") var name: String,
    @field:JsonProperty("code") var code: String,
    @field:JsonProperty("price") var price: Int,
    @field:JsonProperty("version") var version: Long? = null,
)
