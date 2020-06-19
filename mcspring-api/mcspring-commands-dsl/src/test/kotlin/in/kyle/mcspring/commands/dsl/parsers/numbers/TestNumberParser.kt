package `in`.kyle.mcspring.commands.dsl.parsers.numbers

import `in`.kyle.mcspring.commands.dsl.CommandTestSupport.makeBuilder
import `in`.kyle.mcspring.commands.dsl.ContextReciever.BreakParseException
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class TestNumberParser : FreeSpec({

    "between should work" - {
        "between pass" - {
            makeArg {
                between(0, 100) { fail("should not run") }
            } shouldBe 10
        }
        "between fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    between(0, 1) {}
                }
            }
        }
    }

    "greaterThan should work" - {
        "gt pass" - {
            makeArg {
                greaterThan(0) { fail("should not run") }
            } shouldBe 10
        }
        "gt fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    greaterThan(100) {}
                }
            }
        }
    }

    "greaterThanEqual should work" - {
        "gte pass" - {
            makeArg {
                greaterThanEqual(10) { fail("should not run") }
            } shouldBe 10
        }
        "gte fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    greaterThanEqual(100) {}
                }
            }
        }
    }

    "lessThan should work" - {
        "lt pass" - {
            makeArg {
                lessThan(100) { fail("should not run") }
            } shouldBe 10
        }
        "lt fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    lessThan(0) {}
                }
            }
        }
    }

    "lessThanEqual should work" - {
        "lte pass" - {
            makeArg {
                lessThanEqual(10) { fail("should not run") }
            } shouldBe 10
        }
        "lte fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    lessThanEqual(0) {}
                }
            }
        }
    }

    "positive should work" - {
        "positive pass" - {
            makeArg {
                positive { fail("should not run") }
            } shouldBe 10
        }
        "positive fail" - {
            makeArg(-10) {
                shouldThrow<BreakParseException> {
                    positive {}
                }
            }
        }
    }

    "negative should work" - {
        "negative pass" - {
            makeArg(-1) {
                negative { fail("should not run") }
            } shouldBe -1
        }
        "negative fail" - {
            makeArg {
                shouldThrow<BreakParseException> {
                    negative {}
                }
            }
        }
    }

    "nonzero should work" - {
        "nonzero pass" - {
            makeArg {
                nonZero { fail("should not run") }
            } shouldBe 10
        }
        "nonzero fail" - {
            makeArg(value = 0) {
                shouldThrow<BreakParseException> {
                    nonZero {}
                }
            }
        }
    }
})

private fun makeArg(value: Int = 10, lambda: IntParser.() -> Unit) =
        makeBuilder(value.toString()).intArg { parser(lambda) }
