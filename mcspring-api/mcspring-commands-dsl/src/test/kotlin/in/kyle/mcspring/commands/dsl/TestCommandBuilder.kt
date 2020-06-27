package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.runCommand
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class TestCommandBuilder : FreeSpec({

    "require should work" - {
        "require(true) should not fail" - {
            runCommand("test") {
                require({ true }) { fail("should not run") }
                commandComplete()
            }
        }

        "require(false) should fail" - {
            runCommand("test") {
                require({ false }) { message("should run") }
            } shouldBe "should run"
        }
    }

    "missing arg should work" - {
        shouldThrow<TestException> {
            runCommand("") {
                stringArg {
                    missing {
                        throw TestException()
                    }
                }
            }
        }
    }

    "then should only run while executors are enabled" - {
        "test disabled" - {
            shouldThrow<ContextReciever.BreakParseException> {
                CommandBuilder(CommandContext(mockk(), "test", listOf(), mutableListOf(), runExecutors = false)).apply {
                    then {
                        fail("should not run")
                    }
                }
            }
        }
        "test enabled" - {
            shouldThrow<TestException> {
                runCommand("test") {
                    then {
                        throw TestException()
                    }
                }
            }
        }
    }
})
