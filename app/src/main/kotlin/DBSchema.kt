import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.Column

object Calculations: IntIdTable("calculations", "calcid") {
    val expression: Column<String> = text("expression")
    val result: Column<String> = text("result")
    val success: Column<Boolean> = bool("success")
}

class Calculation(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, Calculation>(Calculations)

    var expression by Calculations.expression
    var result by Calculations.result
    var success by Calculations.success
}