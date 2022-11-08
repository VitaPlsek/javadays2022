package cz.vitaplsek.e2e.misc

import io.kotest.core.spec.style.ShouldSpec

class KotestLifecycleTest : ShouldSpec() {

    //    override fun isolationMode() = IsolationMode.InstancePerLeaf
    init {

        beforeSpec { println("| beforeSpec") }
        afterSpec { println("| afterSpec") }
        beforeContainer { println("| beforeContainer") }
        afterContainer { println("| afterContainer") }

        context("top context") {
            println("|| start - in testing")

            beforeContainer { println("|| beforeContainer") }
            afterContainer { println("|| afterContainer") }

            context("nested contest") {
                println("||| start")

                beforeEach { println("||| beforeEach") }
                beforeSpec { println("||| beforeSpec") }

                should("nested should") { println("|||| should") }

                should("nested should 2 ") { println("|||| should 2") }

                println("||| end")
            }

            should("upper should") { println("| should") }
            println("| end - in testing")
        }
    }
}
