package kr.dove.api.core.order

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderService {

    @PostMapping(
        value = ["/order"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun save(@RequestBody order: Order): Mono<Order>

    @GetMapping(
        value = ["/orders"],
        produces = ["application/x-ndjson"]
    )
    fun list(@RequestParam(name = "productId", required = false) productId: Long?,
             @RequestParam(name = "memberId", required = false) memberId: Long?): Flux<Order>
}