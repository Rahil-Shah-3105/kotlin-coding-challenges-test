package com.igorwojda.challenge

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

private fun challenge() {
log.debug(">>> Entering challenge()")

log.debug("<<< Exiting challenge()")
}

private class Test {
	private val log = LoggerFactory.getLogger(Challenge::class.java)
    @Test
    fun `simple test`() {
        val actual = challenge()
        actual shouldBeEqualTo "expected"
    }
}
