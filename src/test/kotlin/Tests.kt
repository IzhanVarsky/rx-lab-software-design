import db.ReactiveMongoDriver.Actions
import model.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URL
import model.Currency.*

class Tests {
    private val port = 8080
    private val server = Server(port)
    private val url = "http://localhost:$port"

    private fun getReq(action: Actions, vararg pairs: Pair<Any, Any>): String {
        val params = pairs.joinToString("&") { it.first.toString() + "=" + it.second.toString() }
        URL("$url${action.value}?$params")
            .openStream()
            .bufferedReader()
            .use { return it.readText() }
    }

    @Test
    fun test() {
        server.startServer().use {
            assertEquals("SUCCESS", getReq(Actions.DROP_USERS))
            assertEquals("SUCCESS", getReq(Actions.DROP_PRODUCTS))

            assertEquals(
                "User #1 inserted with code `SUCCESS`",
                getReq(Actions.REGISTER, id_key to 1, currency_key to RUB)
            )
            assertEquals(
                "User #2 inserted with code `SUCCESS`",
                getReq(Actions.REGISTER, id_key to 2, currency_key to USD)
            )
            assertEquals(
                "User #3 inserted with code `SUCCESS`",
                getReq(Actions.REGISTER, id_key to 3, currency_key to EUR)
            )

            assertEquals(
                "Product `asd` inserted with code `SUCCESS`",
                getReq(Actions.ADD_PRODUCT, name_key to "asd", value_key to 100, currency_key to RUB)
            )
            assertEquals(
                "Product `kek` inserted with code `SUCCESS`",
                getReq(Actions.ADD_PRODUCT, name_key to "kek", value_key to 200, currency_key to USD)
            )
            assertEquals(
                "Product `lol` inserted with code `SUCCESS`",
                getReq(Actions.ADD_PRODUCT, name_key to "lol", value_key to 300, currency_key to EUR)
            )

            assertEquals(
                listOf(
                    Product("asd", RUB.convertTo(RUB, 100.0), RUB),
                    Product("kek", USD.convertTo(RUB, 200.0), RUB),
                    Product("lol", EUR.convertTo(RUB, 300.0), RUB),
                ).joinToString("\n") { it.toString() } + "\n",
                getReq(Actions.GET_PRODUCTS, id_key to 1)
            )

            assertEquals(
                listOf(
                    Product("asd", RUB.convertTo(USD, 100.0), USD),
                    Product("kek", USD.convertTo(USD, 200.0), USD),
                    Product("lol", EUR.convertTo(USD, 300.0), USD),
                ).joinToString("\n") { it.toString() } + "\n",
                getReq(Actions.GET_PRODUCTS, id_key to 2)
            )

            assertEquals(
                listOf(
                    Product("asd", RUB.convertTo(EUR, 100.0), EUR),
                    Product("kek", USD.convertTo(EUR, 200.0), EUR),
                    Product("lol", EUR.convertTo(EUR, 300.0), EUR),
                ).joinToString("\n") { it.toString() } + "\n",
                getReq(Actions.GET_PRODUCTS, id_key to 3)
            )

            assertEquals("SUCCESS", getReq(Actions.DROP_USERS))
            assertEquals("SUCCESS", getReq(Actions.DROP_PRODUCTS))
        }
    }
}