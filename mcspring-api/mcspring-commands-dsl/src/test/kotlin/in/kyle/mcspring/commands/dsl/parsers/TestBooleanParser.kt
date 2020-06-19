package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class TestBooleanParser : FreeSpec({

    "true should parse" - {
        makeBuilder("true").booleanArg { } shouldBe true
        makeBuilder("TrUe").booleanArg { } shouldBe true
    }

    "false should parse" - {
        makeBuilder("false").booleanArg { } shouldBe false
        makeBuilder("FaLsE").booleanArg { } shouldBe false
    }

    "no other string should parse" - {
        Arb.stringPattern("[a-zA-Z]{1,256}").filterNot { it in arrayOf("true", "false") }.checkAll {
            shouldThrow<IllegalStateException> {
                makeBuilder(it).booleanArg { }
            }
        }
    }
})
