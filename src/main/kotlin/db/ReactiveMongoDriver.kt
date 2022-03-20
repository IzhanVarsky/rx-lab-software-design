package db

import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoClient
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import model.*
import rx.Observable

object ReactiveMongoDriver {
    enum class Actions(val value: String, val actionFun: (QueryMap) -> Observable<String>) {
        REGISTER("/register", ::registrationRequest),
        ADD_PRODUCT("/add-product", ::addProductRequest),
        PRODUCT("/product", ::getProductsRequest),
        DROP_USERS("/drop-users", ::dropUsersCollection),
        DROP_PRODUCTS("/drop-products", ::dropProductsCollection),
    }

    private fun addUser(user: User): Observable<Success> =
        userCollection.insertOne(user.toDoc())

    private fun addProduct(product: Product): Observable<Success> =
        productCollection.insertOne(product.toDoc())

    private fun getAllProducts(): Observable<Product> =
        productCollection.find().toObservable().map(::Product)

    private fun getUser(id: Int): Observable<User> = userCollection
        .find(Filters.eq(id_key, id))
        .toObservable().map(::User)

    fun handleActionRequest(action: String, queryMap: QueryMap): Observable<String> =
        Actions.values().find { it.value == action }?.actionFun?.invoke(queryMap)
            ?: throw RuntimeException("Unknown action `$action`")

    private fun dropUsersCollection(queryMap: QueryMap = emptyMap()): Observable<String> =
        userCollection.drop().map { it.name }

    private fun dropProductsCollection(queryMap: QueryMap = emptyMap()): Observable<String> =
        productCollection.drop().map { it.name }

    private fun getProductsRequest(queryMap: QueryMap): Observable<String> =
        getUser(queryMap.getId()).map { it.currency }
            .concatMap { currency: Currency ->
                getAllProducts().map { "${it.productWithNewCurrency(currency)}\n" }
            }

    private fun addProductRequest(queryMap: QueryMap): Observable<String> = with(Product(queryMap)) {
        addProduct(this).map { "Product `$name` inserted with code `$it`" }
    }

    private fun registrationRequest(queryMap: QueryMap): Observable<String> = with(User(queryMap)) {
        addUser(this).map { "User #$id inserted with code `$it`" }
    }

    private val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    private val db
        get() = client.getDatabase("db_rx")
    private val userCollection
        get() = db.getCollection("users")
    private val productCollection
        get() = db.getCollection("products")
}