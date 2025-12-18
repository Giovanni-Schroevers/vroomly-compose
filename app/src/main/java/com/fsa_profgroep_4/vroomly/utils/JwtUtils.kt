package com.fsa_profgroep_4.vroomly.utils

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun isTokenExpired(token: String): Boolean {
        return try {
            val exp = getExpFromToken(token)
            
            if (exp == 0L) {
                true
            } else {
                val currentTimeSeconds = System.currentTimeMillis() / 1000
                currentTimeSeconds >= exp
            }
        } catch (e: Exception) {
            true
        }
    }

    private fun getExpFromToken(token: String): Long {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format")
        }
        
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val decodedString = String(decodedBytes, Charsets.UTF_8)
        
        return JSONObject(decodedString).optLong("exp", 0)
    }
}
