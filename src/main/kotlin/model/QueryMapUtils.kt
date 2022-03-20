package model

typealias QueryMap = Map<String, String>

const val id_key = "id"
const val name_key = "name"
const val value_key = "value"
const val currency_key = "currency"

fun QueryMap.getCurrency() = Currency.valueOf(nonNullGet(currency_key).uppercase())
fun QueryMap.getId() = nonNullGet(id_key).toInt()
fun QueryMap.getName() = nonNullGet(name_key)
fun QueryMap.getValue() = nonNullGet(value_key).toDouble()
fun QueryMap.nonNullGet(key: String): String = this[key]!!