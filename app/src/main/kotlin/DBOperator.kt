import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

data class CalculationRaw(val expr: String, val res: String, val succ: Boolean) {
    override fun toString(): String =
        if (succ) "Calculation: $expr = $res"
        else "Calculation FAILED: $expr -> $res"
}

object DBOperator {
    const val schemaQueryFileName = "cataloog_schema"

    fun extractFileName(fileName: String) =
        Path.of(fileName).nameWithoutExtension
            .removeSuffix(".mv") // nameWithoutExtension убирает .db, но не .mv/.trace
            .removeSuffix(".trace")

    fun initDB(dbFileName: String) {
        connect(dbFileName) // создаёт базу, если она не существует
        executeSQL(schemaQueryFileName) // запускает скрипт инициализации
    }

    fun connect(dbFileName: String) {
        Database.connect("jdbc:h2:./data/${extractFileName(dbFileName)}")
    }

    fun doesDBExist(dbFileName: String) =
        Files.exists(Path.of("./data/${extractFileName(dbFileName)}.mv.db"))

    fun connectOrCreate(dbFileName: String) {
        val didExist = doesDBExist(dbFileName)
        connect(dbFileName) // теперь база создана
        if (!didExist)
            executeSQL(schemaQueryFileName)
    }

    fun executeSQL(queryFileName: String) = transaction {
        @Language("SQL")
        val query = Files.readAllLines(
            Path.of("./sql/${extractFileName(queryFileName)}.sql")
        ).joinToString("\n")

        exec(query)
    }

    // Вызывать эту функцию нужно только внутри транзакции
    private fun addCalculationDirectly(calc: CalculationRaw) {
        Calculation.new {
            expression = calc.expr
            result = calc.res
            success = calc.succ
        }
    }

    fun addCalculation(calc: CalculationRaw) =
        transaction { addCalculationDirectly(calc) }

    fun addCalculation(expr: String, res: String, succ: Boolean) =
        transaction { addCalculationDirectly(CalculationRaw(expr, res, succ)) }

    fun addCalculations(calcs: List<CalculationRaw>) =
        transaction { calcs.forEach(::addCalculationDirectly) }

    fun getCalculations(from: Int, to: Int) = transaction {
        Calculation.all()
            .reversed()
            .drop(from)
            .take(to - from)
            .map { CalculationRaw(it.expression, it.result, it.success) }
    }

    fun getAllCalculations() = transaction {
        Calculation.all()
            .reversed()
            .map { CalculationRaw(it.expression, it.result, it.success) }
    }

    fun clear() = transaction {
        Calculation.all()
            .forEach { it.delete() }
    }

    fun deleteDB(dbFileName: String) {
        // Удаляет базу
        Files.deleteIfExists(Path.of("./data/${extractFileName(dbFileName)}.mv.db"))
        // Удаляет лог
        Files.deleteIfExists(Path.of("./data/${extractFileName(dbFileName)}.trace.db"))
    }
}