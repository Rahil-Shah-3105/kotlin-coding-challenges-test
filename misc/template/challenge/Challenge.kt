package com.igorwojda.challenge

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
private val log = LoggerFactory.getLogger("com.igorwojda.challenge.Challenge")

private fun challenge() {
log.error(">>> Entering challenge()")

log.warn("<<< Exiting challenge()")
}

private class Test {
    @Test
    fun `simple test`() {
        val actual = challenge()
        actual shouldBeEqualTo "expected"
    }
}
