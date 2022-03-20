package model

import org.bson.Document

data class User(val id: Int, val currency: Currency) : Documentable() {
    constructor(document: Document) : this(
        document.getInteger(id_key),
        Currency.valueOf(document.getString(currency_key))
    )

    constructor(queryMap: QueryMap) : this(queryMap.getId(), queryMap.getCurrency())

    override fun dataPairs(): Array<Pair<String, Any>> =
        arrayOf(id_key to id, currency_key to currency.toString())
}