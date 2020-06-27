package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeContext
import `in`.kyle.mcspring.commands.dsl.parsers.numbers.IntParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TestValueBuilder : FreeSpec({

    "default should set the return value" - {
        val context = makeContext("one").first
        val builder = ValueBuilder(context) { IntParser(context, it) }

        builder.apply {
            default { 1 }
            default { 2 }
        }

        builder.returnValue shouldBe 1
    }

    "default should not overwrite existing" - {
        val context = makeContext("one").first
        val builder = ValueBuilder(context) { IntParser(context, it) }
        builder.returnValue = 99

        builder.apply {
            default { 1 }
        }

        builder.returnValue shouldBe 99
    }


    "missing block should run when missing arg" - {
        val context = makeContext("").first
        val builder = ValueBuilder(context) { IntParser(context, it) }

        shouldThrow<TestException> {
            builder.apply {
                missing {
                    throw TestException()
                }
            }
        }
    }
})
