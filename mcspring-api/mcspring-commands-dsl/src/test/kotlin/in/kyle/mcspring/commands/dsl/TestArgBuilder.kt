package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeContext
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec

class TestArgBuilder : FreeSpec({

    class Test(context: CommandContext) : ArgBuilder<Any>(context) {
        override fun build(): ValueArg<Any> {
            return null as ValueArg<Any>
        }
    }

    "invalid block should run properly" - {
        "invalid block runs when invalid" - {
            val test = Test(makeContext("test").first)
            test.returnValue = null

            class TestException : RuntimeException()
            shouldThrow<TestException> {
                test.invalid {
                    throw TestException()
                }
            }
        }

        "invalid block does not run when valid" - {
            val test = Test(makeContext("test", runExecutors = false).first)
            test.returnValue = null
            test.invalid {
                fail("should not run")
            }
        }
    }
})
