package com.usbbog.orientacionvocacional.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

internal class JSONObject private constructor(
    private val values: MutableMap<String, JsonElement>,
) {
    constructor() : this(linkedMapOf())

    constructor(raw: String) : this(
        Json.parseToJsonElement(raw).let { element ->
            require(element is JsonObject)
            element.toMutableMap()
        },
    )

    fun put(name: String, value: Any?): JSONObject = apply {
        values[name] = value.toJsonElement()
    }

    fun has(name: String): Boolean = values.containsKey(name)

    fun isNull(name: String): Boolean = values[name] == null || values[name] is JsonNull

    fun optString(name: String): String =
        values[name]?.takeUnless { it is JsonNull }?.jsonPrimitive?.contentOrNull.orEmpty()

    fun optLong(name: String): Long =
        values[name]?.takeUnless { it is JsonNull }?.jsonPrimitive?.longOrNull ?: 0L

    fun optInt(name: String): Int =
        values[name]?.takeUnless { it is JsonNull }?.jsonPrimitive?.intOrNull ?: 0

    fun optBoolean(name: String, fallback: Boolean = false): Boolean =
        values[name]?.takeUnless { it is JsonNull }?.jsonPrimitive?.booleanOrNull ?: fallback

    fun optJSONArray(name: String): JSONArray? =
        (values[name] as? JsonArray)?.let(::JSONArray)

    override fun toString(): String = JsonObject(values).toString()

    internal fun asJsonElement(): JsonElement = JsonObject(values)

    companion object {
        val NULL: Any = NullValue

        internal fun from(element: JsonObject): JSONObject = JSONObject(element.toMutableMap())
    }
}

internal class JSONArray private constructor(
    private val values: MutableList<JsonElement>,
) {
    constructor() : this(mutableListOf())

    constructor(raw: String) : this(
        Json.parseToJsonElement(raw).let { element ->
            require(element is JsonArray)
            element.toMutableList()
        },
    )

    internal constructor(value: JsonArray) : this(value.toMutableList())

    fun put(value: Any?): JSONArray = apply {
        values += value.toJsonElement()
    }

    fun length(): Int = values.size

    fun getJSONObject(index: Int): JSONObject =
        JSONObject.from(values[index] as JsonObject)

    internal fun asJsonElement(): JsonElement = JsonArray(values)
}

private object NullValue

private fun Any?.toJsonElement(): JsonElement = when (this) {
    null, NullValue -> JsonNull
    is JSONObject -> asJsonElement()
    is JSONArray -> asJsonElement()
    is JsonElement -> this
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    else -> JsonPrimitive(toString())
}
