package com.example.mswproject

import org.junit.Assert.*
import org.junit.Test

class ValidatorTest{
    private val validator:Validator = Validator()

    @Test
    fun ifInputIsBlank_whenIsInputNotEmptyIsCalled_thenTrueIsReturned() {
        assertTrue(validator.isInputEmpty(""))
    }

    @Test
    fun ifInputIsNotADouble_whenIsInputDoubleIsCalled_thenFalseIsReturned() {
        assertFalse(validator.isInputDouble("not a double"))
    }

    @Test
    fun ifInputIsADouble_whenIsInputDoubleIsCalled_thenTrueIsReturned() {
        assertTrue(validator.isInputDouble("33.456356"))
    }

    @Test
    fun ifInputIsGreaterThan50000_whenIsRadiusValidIsCalled_thenFalseIsReturned() {
        assertFalse(validator.isRadiusValid("51000"))
    }

    @Test
    fun ifInputIsLessThanThan50000_whenIsRadiusValidIsCalled_thenTrueIsReturned() {
        assertTrue(validator.isRadiusValid("49000"))
    }
}