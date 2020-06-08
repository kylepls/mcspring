package `in`.kyle.mcspring.commands.dsl.builders

import `in`.kyle.mcspring.commands.dsl.CommandBuilder
import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeContext
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class TestBaseRequirementBuilder {

    @Test
    fun testRequirePass() {
        testCommand("test") {
            require({ true }) { fail("should not run") }
        }
    }

    @Test
    fun testRequireFail() {
        testCommand("test") {
            require({ false }) { message("should run") }
        } shouldBeEqualTo "should run"
    }

    @Test
    fun testDefaultSet() {
        testCommand("") {
            val test by stringArg {
                default { "test" }
                default { "other" }
            }
            test shouldBeEqualTo "test"
        }
    }

    @Test
    fun testMissing() {
        testCommand("") {
            val test by stringArg {
                missing { message("should run") }
                invalid { fail("should not run") }
            }
        } shouldBeEqualTo "should run"
    }

    @Test
    fun testInvalid() {
        testCommand("test") {
            val test by intArg {
                missing { fail("should not run") }
                invalid { message("should run") }
            }
        } shouldBeEqualTo "should run"
    }

    private fun testCommand(commandString: String, lambda: CommandBuilder.() -> Unit): String {
        val (context, messages) = makeContext(commandString)
        commandExecutor(lambda).provider(context)
        return messages.joinToString(" ")
    }
}
