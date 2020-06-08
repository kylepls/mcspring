package `in`.kyle.mcspring.commands.dsl.builders

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.runCommand
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class TestStringRequirementBuilder {

    @Test
    fun testSingleString() {
        runCommand("test") {
            val arg0 by stringArg {  }
            arg0 shouldBeEqualTo "test"
        }
    }

    @Test
    fun testStringMissing() {
        runCommand("") {
            val arg0 by stringArg {
                missing { message("missing arg") }
                invalid { fail("should not run") }
            }
            fail("should not run")
        } shouldBeEqualTo "missing arg"
    }

    @Test
    fun testStringMulti() {
        runCommand("arg1 arg2") {
            val arg0 by stringArg {
                missing { fail("should not run") }
                invalid { fail("should not run") }
            }

            arg0 shouldBeEqualTo "arg1"
            context.argIndex shouldBeEqualTo 1

            val arg1 by stringArg {
                missing { fail("should not run") }
                invalid { fail("should not run") }
            }
            arg1 shouldBeEqualTo "arg2"
        }
    }
}
