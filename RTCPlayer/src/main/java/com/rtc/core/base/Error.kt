package com.rtc.core.base

enum class Error(val code: Int, val message: String) {
    ERROR_CODE_SUCCESS(0, "操作成功"),
    //1开头的，应用层错误
    ERROR_CODE_PARSE_FAILED(10001, "解析异常"),
}