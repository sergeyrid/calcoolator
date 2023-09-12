import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path

data class CalculationRaw(val expr: String, val res: String, val succ: Boolean)

object DBOperator {
    fun initDB(dbFileName: String, schemaQueryFileName: String) {
        Database.connect("jdbc:h2:./data/${
            dbFileName
                .trim()
                .removeSuffix(".db")
                .removeSuffix(".mv")
        }", driver = "org.h2.Driver")
        transaction {
            @Language("SQL")
            val dbSchema = Files.readAllLines(
                Path.of("./sql/${
                    schemaQueryFileName.trim().removeSuffix(".sql")
                }.sql")
            ).joinToString("\n")

            exec(dbSchema)
        }
    }

    fun connect(dbFileName: String) {
        Database.connect("jdbc:h2:./data/$dbFileName")
    }

    fun executeSQL(queryFileName: String) = transaction {
        @Language("SQL")
        val query = Files.readAllLines(
            Path.of("./sql/${
                queryFileName.trim().removeSuffix(".sql")
            }.sql")
        ).joinToString("\n")

        exec(query)
    }

    fun addCalculation(expr: String, res: String, succ: Boolean) = transaction {
        Calculation.new {
            expression = expr
            result = res
            success = succ
        }
    }

    fun addCalculation(calc: CalculationRaw) =
        addCalculation(calc.expr, calc.res, calc.succ)

    fun addCalculations(calcs: List<CalculationRaw>) = transaction {
        for (calc in calcs) {
            Calculation.new {
                expression = calc.expr
                result = calc.res
                success = calc.succ
            }
        }
    }

    fun getCalculations(num: Int) = transaction {
        Calculation.all()
            .reversed()
            .take(num)
            .map { CalculationRaw(it.expression, it.result, it.success) }
    }

    fun getAllCalculations() = transaction {
        Calculation.all()
            .reversed()
            .map { CalculationRaw(it.expression, it.result, it.success) }
    }
}