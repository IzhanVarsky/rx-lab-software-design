import db.ReactiveMongoDriver.handleActionRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServer
import rx.Observable
import java.io.Closeable

class Server(port: Int) : Closeable {
    private val httpServer = HttpServer.newServer(port)

    fun startServer(): Server = apply {
        httpServer.start { request, response ->
            val responseMessage: Observable<String> =
                try {
                    handleActionRequest(
                        request.decodedPath,
                        request.queryParameters.mapValues { it.value.first() }
                    )
                } catch (e: Exception) {
                    response.status = HttpResponseStatus.BAD_REQUEST
                    Observable.just(e.message)
                }
            response.writeString(responseMessage)
        }
    }

    fun awaitShutdown() = apply { httpServer.awaitShutdown() }

    override fun close() {
        httpServer.shutdown()
    }
}
