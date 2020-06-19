package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeBuilder
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TestMapParser : FreeSpec({

    "test map vararg" - {
        makeBuilder("one").mapArg<Int> {
            parser {
                map("one" to 1)
            }
        } shouldBe 1
    }

    "test map map" - {
        makeBuilder("one").mapArg<Int> {
            parser {
                map(mapOf("one" to 1))
            }
        } shouldBe 1
    }
})
