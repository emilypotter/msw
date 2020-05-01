package com.example.mswproject

class Validator {
    fun isInputEmpty(input: String): Boolean {
        return input.isNullOrBlank()
    }

    fun isInputDouble(input: String): Boolean {
        //if not null then double
        return input.toDoubleOrNull() != null
    }

    fun isInputInt(input: String): Boolean {
        //if not null then int
        return input.toIntOrNull() != null
    }

    fun isRadiusValid(input: String): Boolean {
        return input.toInt() <= 50000
    }
}