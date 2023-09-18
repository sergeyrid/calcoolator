import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.io.*

object Server {
    private const val DBFilename: String = "calculatorDB"

    fun launch() {
        embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
            println("Server try connect to database")
            DBOperator.connectOrCreate(DBFilename)

            println("Server is ready")

            install(ContentNegotiation) {
                json()
            }
            routing {
                post("/api/calculate") {
                    val expr = call.receiveParameters()["query"].toString()
                    try {
                        val result = Calculator.calculate(expr)
                        println(result)

                        DBOperator.addCalculation(expr, result.toString(), true)
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("type" to "success", "result" to result.toString())
                        )
                    } catch (e: Exception) {
                        val errorInfo: String = e.message ?: "Unknown Error"
                        println(errorInfo)

                        DBOperator.addCalculation(expr, errorInfo, false)
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("type" to "error", "message" to errorInfo)
                        )
                    }
                }

                get("/api/history") {
//                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
//                    val before = call.request.queryParameters["before"]?.toIntOrNull()
//                        ?: DBOperator.getAllCalculationsCount() + 1

                    call.respond(DBOperator.getAllCalculations()
                        .map { mapOf("query" to it.expr)})
                }
            }
        }.start(wait = true)
    }
}
