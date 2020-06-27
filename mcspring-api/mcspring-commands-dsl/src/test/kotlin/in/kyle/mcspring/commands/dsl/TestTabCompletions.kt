package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.getCompletions
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TestTabCompletions : FreeSpec({
    "should return completions for command" - {
        getCompletions("") {
            tabCompletions("one", "two", "three")
            commandMissing()
        } shouldBe listOf("one", "two", "three")
    }

    "should return partial completions" - {
        getCompletions("on") {
            tabCompletions("one", "two", "three")
            commandMissing()
        } shouldBe listOf("one")
    }

    "completed commands return no completions" - {
        getCompletions("") {
            tabCompletions("one", "two", "three")
            commandComplete()
        } shouldBe emptyList()
    }

    "multi-arg commands should reset completions" - {
        getCompletions("one") {
            mapArg<Int> {
                parser {
                    map("one" to 1)
                }
            }
            mapArg<Int> {
                parser {
                    map("two" to 1)
                }
                missing {}
            }

            commandComplete()
        } shouldBe emptyList()
    }

    "invalid arg values should stop parsing" - {
        getCompletions("on tw") {
            mapArg<Int> {
                parser {
                    map("one" to 1)
                }
                invalid {}
            }

            mapArg<Int> {
                parser {
                    map("two" to 1)
                }
                missing {}
            }

            commandComplete()
        } shouldBe emptyList()
    }
})
