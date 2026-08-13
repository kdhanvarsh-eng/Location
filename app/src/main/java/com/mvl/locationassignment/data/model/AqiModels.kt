package com.mvl.locationassignment.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

@JsonAdapter(AqiResponseDeserializer::class)
data class AqiResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: AqiData?
)

data class AqiData(
    @SerializedName("aqi")
    val aqi: Int,
    @SerializedName("city")
    val city: CityInfo?
)

data class CityInfo(
    @SerializedName("name")
    val name: String?
)

data class AqiInfo(
    val aqi: Int,
    val cityName: String,
    val isError: Boolean = false
)

// Custom deserializer to handle both success and error responses
class AqiResponseDeserializer : JsonDeserializer<AqiResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AqiResponse {
        val jsonObject = json.asJsonObject
        val status = jsonObject.get("status").asString
        
        // If status is "ok", data is an object; if "error", data is a string
        val dataElement = jsonObject.get("data")
        val aqiData = if (dataElement != null && !dataElement.isJsonPrimitive) {
            // data is an object
            context?.deserialize<AqiData>(dataElement, AqiData::class.java)
        } else {
            // data is a string (error message) or null
            null
        }
        
        return AqiResponse(
            status = status,
            data = aqiData
        )
    }
}
