package com.example.hermes.domain.models

import java.util.*

class Order(
    val uid: String,
    val number: String,
    val date: String,
    val amount: Long,
    val quantity: Long,
    val shop: Shop,
    val client: Client,
    val address: Address?,
    val products: List<Product>,
    val comment: String,
    var status: Status,
    val method: Method
)
{
    enum class Status(var key: String) {
        NEW("Новый"),
        PROCESSED("Обработан"),
        ASSEMBLED("Собран"),
        SENT("Отправлен"),
        COMPLETED("Выполнен"),
        CANCELLED("Отменен"),
        REFUND("Возврат")
    }

    enum class Method(var key: String) {
        DELIVERY("Доставка"),
        PICKUP("Самовывоз")
    }
}