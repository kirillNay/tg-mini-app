package com.kirillNay.telegram.miniapp.webApp.internal

// Low-level helpers shared by the js and wasmJs targets. Kotlin/Wasm has no `dynamic`,
// so every untyped access to a JS value goes through these small `js()` functions.

internal fun telegramWebAppJsOrNull(): WebAppJs? =
    js("(typeof window !== 'undefined' && window.Telegram && window.Telegram.WebApp) || null")

internal fun newJsObject(): JsAny = js("({})")

internal fun jsSet(obj: JsAny, key: String, value: JsAny?) {
    js("obj[key] = value;")
}

internal fun jsGet(obj: JsAny, key: String): JsAny? = js("obj[key]")

internal fun jsTypeOf(value: JsAny?): String = js("typeof value")

internal fun jsToString(value: JsAny): String = js("String(value)")

internal fun jsToNumber(value: JsAny): Double = js("Number(value)")

internal fun jsIsTrue(value: JsAny?): Boolean = js("value === true")

internal fun jsObjectKeys(value: JsAny): JsArray<JsString> = js("Object.keys(value)")

internal fun consoleLog(message: String) {
    js("console.log(message);")
}

/** Wraps a Kotlin lambda into a single JS function object, so the same reference can be passed to `on*` and `off*`. */
internal fun jsFunction0(block: () -> Unit): JsAny = js("(function () { block(); })")

/** Same as [jsFunction0] for handlers receiving one argument. */
internal fun jsFunction1(block: (JsAny?) -> Unit): JsAny = js("(function (arg) { block(arg); })")

internal fun JsAny?.isNullOrUndefined(): Boolean = this == null || jsTypeOf(this) == "undefined"

internal fun JsAny?.asStringOrNull(): String? = if (isNullOrUndefined()) null else jsToString(this!!)

internal fun JsAny?.asDoubleOrNull(): Double? = when (if (isNullOrUndefined()) null else jsTypeOf(this)) {
    "number" -> jsToNumber(this!!)
    "string" -> jsToString(this!!).toDoubleOrNull()
    else -> null
}

internal fun JsAny?.asLongOrNull(): Long? = asDoubleOrNull()?.toLong()

internal fun JsAny?.asIntOrNull(): Int? = asDoubleOrNull()?.toInt()

internal fun JsAny?.isTrue(): Boolean = jsIsTrue(this)

/** Kotlin/Wasm turns an undefined `Boolean?` external property into false, so optional flags are read as JsAny?. */
internal fun JsAny?.asBooleanOrNull(): Boolean? = if (!isNullOrUndefined() && jsTypeOf(this) == "boolean") jsIsTrue(this) else null

@Suppress("REDUNDANT_CALL_OF_CONVERSION_METHOD") // JsString is String on the js target only
internal fun JsArray<JsString>.toStringList(): List<String> = toList().map { it.toString() }

internal fun List<String>.toJsStringArray(): JsArray<JsString> = map { it.toJsString() }.toJsArray()

/** Builds a plain JS object. Properties with `null` values are skipped, because telegram-web-app.js treats `null` as a set value. */
internal class JsObjectBuilder(val obj: JsAny) {

    fun put(key: String, value: String?) {
        if (value != null) jsSet(obj, key, value.toJsString())
    }

    fun put(key: String, value: Boolean?) {
        if (value != null) jsSet(obj, key, value.toJsBoolean())
    }

    fun put(key: String, value: Int?) {
        if (value != null) jsSet(obj, key, value.toJsNumber())
    }

    fun put(key: String, value: JsAny?) {
        if (value != null) jsSet(obj, key, value)
    }
}

internal inline fun jsObject(build: JsObjectBuilder.() -> Unit): JsAny =
    JsObjectBuilder(newJsObject()).apply(build).obj
