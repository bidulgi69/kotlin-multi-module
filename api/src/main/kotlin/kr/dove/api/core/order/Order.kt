package kr.dove.api.core.order

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kr.dove.api.core.cart.Cart
import kr.dove.api.core.member.Member
import kr.dove.api.core.shop.Shop

data class Order(
    @field:JsonIgnoreProperties var transactionId: String? = null,
    @field:JsonProperty("order_id") var orderId: String? = null,
    @field:JsonProperty("order_identification") var identification: String? = null,
    @field:JsonIgnoreProperties var created: Long? = null,
    @field:JsonProperty("shop") val shop: Shop,
    @field:JsonProperty("member") val member: Member,
    @field:JsonProperty("cart") val cart: List<Cart>,
    @field:JsonProperty("version") var version: Long? = null,
)
