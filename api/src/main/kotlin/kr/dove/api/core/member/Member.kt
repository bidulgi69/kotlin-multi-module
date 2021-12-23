package kr.dove.api.core.member

import com.fasterxml.jackson.annotation.JsonProperty

data class Member(
    @field:JsonProperty("member_id") var memberId: Long? = null,
    @field:JsonProperty("name") var name: String,
    @field:JsonProperty("id") val id: String,
    @field:JsonProperty("version") var version: Long? = null,
)
