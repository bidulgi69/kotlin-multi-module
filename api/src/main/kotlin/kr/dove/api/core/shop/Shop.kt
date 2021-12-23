package kr.dove.api.core.shop

import com.fasterxml.jackson.annotation.JsonProperty

data class Shop(
    @field:JsonProperty("shop_id") var shopId: Long? = null,
    @field:JsonProperty("name") var name: String,
    @field:JsonProperty("location") var location: String,
    @field:JsonProperty("version") var version: Long? = null,
)
