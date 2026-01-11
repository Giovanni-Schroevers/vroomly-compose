package com.fsa_profgroep_4.vroomly.data.auth

/**
 * Custom exception to carry field-specific validation errors from the API.
 * @param fieldErrors A map where keys are field names (e.g., "email", "password")
 *                    and values are the corresponding error messages.
 * @param message A general error message for logging/fallback purposes.
 */
class ValidationException(
    val fieldErrors: Map<String, String>,
    message: String = "Validation failed"
) : Exception(message)
