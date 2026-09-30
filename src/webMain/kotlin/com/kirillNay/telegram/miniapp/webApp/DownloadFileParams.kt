package com.kirillNay.telegram.miniapp.webApp

import com.kirillNay.telegram.miniapp.webApp.internal.jsObject

/**
 * Bot API 8.0+
 *
 * Parameters of a file download request made with [WebApp.downloadFile].
 *
 * For consistent behavior across platforms the server should respond with the headers
 * `Content-Disposition: attachment; filename="<file_name>"` and `Access-Control-Allow-Origin: https://web.telegram.org`.
 *
 * @param url the HTTPS URL of the file to be downloaded.
 * @param fileName the suggested name for the downloaded file.
 */
data class DownloadFileParams(
    val url: String,
    val fileName: String,
) {

    internal fun toJs(): JsAny = jsObject {
        put("url", url)
        put("file_name", fileName)
    }
}
