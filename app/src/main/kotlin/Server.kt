package server

import db.DBOperator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.io.*


object Server {
    const val dbname: String = "DB"
    fun Application() {
        embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
            println("Server has started its work")
            routing {
                get("/api/history?limit={N}&before={ID}") {
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                    val before = call.request.queryParameters["before"]?.toIntOrNull()

                    DBOperator.initDB(dbname)
                    if (before != null) {
                        call.respond(HttpStatusCode.OK, DBOperator.getCalculations(before - 10, before))
                    }
                    else {
                        call.respond(HttpStatusCode.OK, DBOperator.getCalculations(1, limit))
                    }
                }

//                post("/api/calculate") {
//                    val request = call.request<CalculationRequest>()
//                    val query = request.query
//                    val result = Calculation.calculate(query)
//                    if (result) {
//                        call.respond("success", result)
//                    } else {
//                        call.respond("error", result)
//                    }
//                }
            }
        }.start(wait = true)

        println("Completed")
    }

}
