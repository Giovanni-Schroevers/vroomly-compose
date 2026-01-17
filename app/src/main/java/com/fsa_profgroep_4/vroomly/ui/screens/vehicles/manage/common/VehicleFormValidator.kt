package com.fsa_profgroep_4.vroomly.ui.screens.vehicles.manage.common

import java.util.Calendar

object VehicleFormValidator {

    fun validateRequired(value: String, errorMessage: String): String? {
        return if (value.isBlank()) errorMessage else null
    }

    fun validateLicensePlate(value: String, requiredError: String, formatError: String): String? {
        if (value.isBlank()) return requiredError
        // Basic license plate validation - at least 4 characters, alphanumeric with dashes
        val pattern = Regex("^[A-Z0-9][A-Z0-9\\-]{2,}[A-Z0-9]$", RegexOption.IGNORE_CASE)
        if (!pattern.matches(value.replace(" ", ""))) return formatError
        return null
    }

    fun validateYear(value: String, requiredError: String, invalidError: String): String? {
        if (value.isBlank()) return requiredError
        val year = value.toIntOrNull() ?: return invalidError
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year < 1900 || year > currentYear + 1) return invalidError
        return null
    }

    fun validateSeats(value: String, requiredError: String, invalidError: String): String? {
        if (value.isBlank()) return requiredError
        val seats = value.toIntOrNull() ?: return invalidError
        if (seats < 1 || seats > 50) return invalidError
        return null
    }

    fun validatePositiveNumber(value: String, requiredError: String, invalidError: String): String? {
        if (value.isBlank()) return requiredError
        val num = value.toDoubleOrNull() ?: return invalidError
        if (num < 0) return invalidError
        return null
    }

    fun validateVin(value: String, requiredError: String, formatError: String): String? {
        if (value.isBlank()) return requiredError
        // VIN must be exactly 17 alphanumeric characters (no I, O, Q)
        val vinPattern = Regex("^[A-HJ-NPR-Z0-9]{17}$", RegexOption.IGNORE_CASE)
        if (!vinPattern.matches(value)) return formatError
        return null
    }
}
