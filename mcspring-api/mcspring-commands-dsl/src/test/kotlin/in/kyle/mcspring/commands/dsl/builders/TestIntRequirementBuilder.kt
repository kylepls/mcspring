package `in`.kyle.mcspring.commands.dsl.builders

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.runCommand
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.fail

class TestIntRequirementBuilder {

    @Test
    fun testInt() {
        runCommand("123") {
            val arg0 by intArg {
                invalid { fail("should not run") }
                missing { fail("should not run") }
            }
            arg0 shouldBeEqualTo 123
        }
    }
}
