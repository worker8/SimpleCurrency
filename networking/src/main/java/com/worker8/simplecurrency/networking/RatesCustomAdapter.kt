package com.worker8.simplecurrency.networking

import com.squareup.moshi.*
import java.lang.reflect.Type

class RatesCustomAdapter : JsonAdapter<Rates>() {
    override fun fromJson(reader: JsonReader): Rates? {
        val list = mutableListOf<ConversionRate>()

        reader.beginObject()
        while (reader.hasNext()) {
            list.add(
                ConversionRate(
                    code = reader.nextName(),
                    rate = reader.nextString().toDouble()
                )
            )
        }
        reader.endObject()
        return Rates().apply {
            conversionRates = list
        }
    }

    override fun toJson(writer: JsonWriter, value: Rates?) {
        //TODO: write this if needed
    }
}

class ConversionsFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (Types.getRawType(type).isAssignableFrom(Rates::class.java)) {
            return RatesCustomAdapter()
        }
        return null
    }
}
