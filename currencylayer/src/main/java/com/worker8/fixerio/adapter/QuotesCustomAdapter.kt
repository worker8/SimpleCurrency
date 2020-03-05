package com.worker8.fixerio.adapter

import com.squareup.moshi.*
import com.worker8.fixerio.model.ConversionRate
import com.worker8.fixerio.model.Quotes

class QuotesCustomAdapter : JsonAdapter<Quotes>() {
    override fun fromJson(reader: JsonReader): Quotes? {
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
        return Quotes().apply {
            conversionRates = list
        }
    }

    override fun toJson(writer: JsonWriter, value: Quotes?) {
        //TODO: write this if needed
    }
}

//class ConversionsFactory : JsonAdapter.Factory {
//    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
//        if (Types.getRawType(type).isAssignableFrom(Quotes::class.java)) {
//            return QuotesCustomAdapter()
//        }
//        return null
//    }
//
//}
