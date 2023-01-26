package test.android.sberpay

data class SberWebResponse(val orderId: String, val formUrl: String)

data class SberPayResponse(val orderId: String, val sbolBankInvoiceId: String)
