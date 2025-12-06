package com.example.sikembang.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    const val SUPABASE_URL = "https://cpimeunmekkfubpapzeq.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNwaW1ldW5tZWtrZnVicGFwemVxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUwMDQyMjgsImV4cCI6MjA4MDU4MDIyOH0.66fD61lHI3yUQsyyYXXKHZRXAfQrOmn2yJSWpnj-lKA"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest.Companion)
        install(Storage.Companion)
    }
}