package model

import org.bson.Document

data class Product(val name: String, private val value: Double, private val currency: Currency) : Documentable() {
    constructor(doc: Document) : this(
        doc.getString(name_key),
        doc.getDouble(value_key),
        Currency.valueOf(doc.getString(currency_key))
    )

    constructor(queryMap: QueryMap) : this(
        queryMap.getName(),
        queryMap.getValue(),
        queryMap.getCurrency()
    )

    override fun dataPairs(): Array<Pair<String, Any>> = arrayOf(
        name_key to name,
        value_key to value,
        currency_key to currency.toString()
    )

    fun productWithNewCurrency(newCurrency: Currency): Product =
        Product(name, currency.convertTo(newCurrency, value), newCurrency)
}