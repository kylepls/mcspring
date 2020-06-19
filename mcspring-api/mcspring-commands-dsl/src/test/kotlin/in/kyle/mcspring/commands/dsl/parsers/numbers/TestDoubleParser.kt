package `in`.kyle.mcspring.commands.dsl.parsers.numbers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class TestDoubleParser : FreeSpec({
    "should parse simple double" - {
        makeBuilder("0.0001").doubleArg { } shouldBe 0.0001
    }

    "should parse all doubles" - {
        checkAll<Double> {
            makeBuilder(it.toString()).doubleArg { } shouldBe it
        }
    }
})
