package kr.dove.api.core.cart

import com.fasterxml.jackson.annotation.JsonProperty
import kr.dove.api.core.product.Product

data class Cart(
    @field:JsonProperty("cart_id") var cartId: Long? = null,
    @field:JsonProperty("product") var product: Product,
    @field:JsonProperty("count") var count: Int = 1,
    @field:JsonProperty("version") var version: Long? = null,
)
