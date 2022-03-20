package model

enum class Currency(private val cost: Double) {
    RUB(1.0),
    USD(103.95),
    EUR(114.8);

    fun convertTo(to: Currency, value: Double) = value * cost / to.cost
}