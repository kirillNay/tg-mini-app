package nay.kirill.telegram_kmp.mini_app.webApp

enum class InvoiceStatus(val value: String) {

    /**
     * Invoice was paid successfully
     */
    PAID("paid"),

    /**
     * User closed this invoice without paying
     */
    CANCELLED("cancelled"),

    /**
     * User tried to pay, but the payment was failed
     */
    FAILED("failed"),

    /**
     * the payment is still processing. The bot will receive a service message about a [successful payment](https://core.telegram.org/bots/api#successfulpayment) when the payment is successfully paid.
     */
    PENDING("pending");

    companion object {

        fun getValue(value: String): InvoiceStatus = InvoiceStatus.values().find { it.value == value }
            ?: error("Unknown invoice status")
    }

}