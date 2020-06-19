package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.runCommand
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import org.junit.jupiter.api.fail

class TestStringParser : FreeSpec({

    "simple string parses" - {
        runCommand("test") {
            val arg0 = stringArg {  }
            arg0 shouldBe "test"
        }
    }

    "missing called on empty string" - {
        runCommand("") {
            val arg0 = stringArg {
                missing { message("missing arg") }
                invalid { fail("should not run") }
            }
            fail("should not run")
        } shouldBe "missing arg"
    }

    "multiple strings are parses correctly" - {
        runCommand("arg1 arg2") {
            val arg0 = stringArg {
                missing { fail("should not run") }
                invalid { fail("should not run") }
            }

            arg0 shouldBe "arg1"
            context.argIndex shouldBe 1

            val arg1 = stringArg {
                missing { fail("should not run") }
                invalid { fail("should not run") }
            }
            arg1 shouldBe "arg2"
        }
    }

    "test all strings parse correctly" - {
        Arb.stringPattern("[^ ]{1,256}").checkAll {
            runCommand(it) {
                val arg1 = stringArg {
                    missing { fail("should not run") }
                    invalid { fail("should not run") }
                }
                arg1 shouldBe it
            }
        }
    }
})
