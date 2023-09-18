import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.io.*

@Serializable
data class CalculateRequest(val query: String)

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
                staticFiles("/", File("client"))

                post("/api/calculate") {
                    val req = call.receive<CalculateRequest>() //! check for errors
                    val expr = req.query
                    try {
                        val result = Calculator.calculate(expr)
                        println(result)

                        DBOperator.addCalculation(expr, result.toString(), true)
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("type" to "success", "result" to result.toString()),
                        )
                    } catch (e: Exception) {
                        val errorInfo: String = e.message ?: "Unknown Error"
                        println(errorInfo)

                        DBOperator.addCalculation(expr, errorInfo, false)
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("type" to "error", "message" to errorInfo),
                        )
                    }
                }

                get("/api/history") {
//                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
//                    val before = call.request.queryParameters["before"]?.toIntOrNull()
//                        ?: DBOperator.getAllCalculationsCount() + 1

                    call.respond(
                        DBOperator.getAllCalculations()
                            .map { mapOf("query" to it.expr) },
                    )
                }
            }
        }.start(wait = true)
    }
}
