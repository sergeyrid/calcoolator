package server

import db.DBOperator
import calc.Calculator
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
                    val calculations = DBOperator.getAllCalculations()
                    val startIndex = before?.let { it - 10 } ?: 1
                    val endIndex = before ?: (startIndex + limit)
                    val results = calculations.subList(startIndex - 1, endIndex).take(limit)

                    call.respond(results)
                }

                post("/api/calculate") {
                    val request = call.request<CalculationRequest>()
                    val query = request.query
                    val result = Calculator.calculate(query)
                    if (result) {
                        call.respond(true, result)
                    } else {
                        call.respond(false, result)
                    }
                }
            }
        }.start(wait = true)

        println("Completed")
    }

}
