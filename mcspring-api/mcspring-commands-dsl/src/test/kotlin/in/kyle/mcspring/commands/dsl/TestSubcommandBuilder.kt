package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeContext
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TestSubcommandBuilder : FreeSpec({

    "should run missing block" - {
        val context = makeContext("").first
        val builder = SubcommandBuilder(context)

        builder.apply {
            shouldThrow<ContextReciever.BreakParseException> {
                missing {}
            }
        }
    }

    "should run invalid block" - {
        val context = makeContext("arg1").first
        val builder = SubcommandBuilder(context)

        builder.apply {
            shouldThrow<ContextReciever.BreakParseException> {
                invalid {}
            }
        }
    }

    "should run on block" - {
        val context = makeContext("sub1").first
        val builder = SubcommandBuilder(context)

        builder.apply {
            shouldThrow<ContextReciever.BreakParseException> {
                on("sub1") {
                    context.argIndex shouldBe 1
                }
            }
        }
    }
})
