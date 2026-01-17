package com.fsa_profgroep_4.vroomly.data.storage

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.util.UUID

object SupabaseConfig {
    const val SUPABASE_URL = "https://rfpeflpyaztxbzqfaoel.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJmcGVmbHB5YXp0eGJ6cWZhb2VsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAxNDU5MzgsImV4cCI6MjA3NTcyMTkzOH0.3uI17btAURDWQCd2YCUOOQXSDnALlZEwXu7OGGdU_EQ"
    const val BUCKET_NAME = "vehicle-images"
}

class ImageStorageService(
    private val supabaseClient: SupabaseClient
) {
    suspend fun uploadVehicleImage(vehicleId: Int, imageBytes: ByteArray): Result<String> {
        return runCatching {
            val fileName = "${UUID.randomUUID()}.jpg"
            val path = "vehicle-$vehicleId/$fileName"

            supabaseClient.storage
                .from(SupabaseConfig.BUCKET_NAME)
                .upload(path, imageBytes)

            supabaseClient.storage
                .from(SupabaseConfig.BUCKET_NAME)
                .publicUrl(path)
        }
    }
}

fun createSupabaseClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
    ) {
        install(Storage)
    }
}
