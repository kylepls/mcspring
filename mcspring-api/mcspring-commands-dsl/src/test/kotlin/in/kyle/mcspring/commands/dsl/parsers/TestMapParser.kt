package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.getCompletions
import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeBuilder
import `in`.kyle.mcspring.commands.dsl.TestException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.lang.RuntimeException

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

    "missing map arg should not error" - {
        shouldThrow<TestException> {
            makeBuilder("").mapArg<Int> {
                parser {
                    map("one" to 1)
                    map("two" to 2)
                    map("three" to 3)
                }
                missing {
                    throw TestException()
                }
            }
        }
    }

    "test tab completions" - {
        getCompletions(" ") {
            mapArg<Int> {
                parser {
                    map("one" to 1)
                    map("two" to 2)
                    map("three" to 3)
                }
                invalid { }
                missing { }
            }
        } shouldBe listOf("one", "two", "three")
    }
})
