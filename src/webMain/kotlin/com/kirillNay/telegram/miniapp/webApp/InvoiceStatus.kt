package com.kirillNay.telegram.miniapp.webApp

/**
 * Status of an invoice opened with [WebApp.openInvoice].
 */
enum class InvoiceStatus(val value: String) {

    /** Invoice was paid successfully. */
    PAID("paid"),

    /** User closed this invoice without paying. */
    CANCELLED("cancelled"),

    /** User tried to pay, but the payment failed. */
    FAILED("failed"),

    /** The payment is still processing. The bot will receive a service message about a successful payment. */
    PENDING("pending"),

    /** A status this library version doesn't know about. */
    UNKNOWN("");

    internal companion object {

        fun from(value: String?): InvoiceStatus = entries.find { it.value == value && it != UNKNOWN } ?: UNKNOWN
    }
}
