package com.fsa_profgroep_4.vroomly.data.adapter

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import kotlinx.datetime.LocalDate

object DateAdapter : Adapter<LocalDate> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): LocalDate {
        val dateString = reader.nextString()!!
        return LocalDate.parse(dateString)
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: LocalDate) {
        writer.value(value.toString())
    }
}
