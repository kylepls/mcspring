package `in`.kyle.mcspring.commands.dsl.builders

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.runCommand
import org.amshove.kluent.shouldBeEqualTo
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test

class TestOnRequirementBuilder {

    @Test
    fun testSubcommandMissing() {
        runCommand("") {
            subcommand {
                missing { message("should run") }
                invalid { fail("should not run") }
            }
            fail("should not run")
        } shouldBeEqualTo "should run"
    }

    @Test
    fun testSubcommandInvalid() {
        runCommand("arg1") {
            subcommand {
                missing { fail("should not run") }
                invalid { message("should run") }
            }
            fail("should not run")
        } shouldBeEqualTo "should run"
    }

    @Test
    fun testSubcommandSimple() {
        runCommand("sub1") {
            subcommand {
                on("sub1") {
                    context.argIndex shouldBeEqualTo 1
                }
                missing { fail("should not run") }
                invalid { fail("should not run") }
            }
        }
    }
}
