package com.fsa_profgroep_4.vroomly.ui.models

data class FormField(
    val value: String = "",
    val error: String? = null,
) {
    fun validateRequired(errorMessage: String): FormField {
        return this.copy(error = if (this.value.isBlank()) errorMessage else null)
    }
}
