package com.igorwojda.challenge

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private fun challenge() {
private val log = LoggerFactory.getLogger(Challenge::class.java)

log.debug(">>> Entering challenge()")
}

private class Test {
    @Test
    fun `simple test`() {
        val actual = challenge()
        actual shouldBeEqualTo "expected"
    }
}
