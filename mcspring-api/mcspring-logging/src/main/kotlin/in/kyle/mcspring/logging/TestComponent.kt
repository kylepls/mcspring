package `in`.kyle.mcspring.logging

import org.springframework.stereotype.Component

@Component
open class TestComponent {

    @LogCall
    fun test() {
        println("called")
    }
}
