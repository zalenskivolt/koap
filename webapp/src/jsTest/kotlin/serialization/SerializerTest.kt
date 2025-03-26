@file:UseSerializers(
    OptionSerializer::class,
)

package com.juul.koap.serialization

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPort
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.Test

class SerializerTest {
    @Test
    fun optionSerializesTo() {
        // special serialization, content format:
        assertOptionSerializesTo(ContentFormat.PlainText, "Content-Format: text/plain; charset=utf-8")
        assertOptionSerializesTo(ContentFormat.JSON, "Content-Format: application/json")
        assertOptionSerializesTo(ContentFormat.CBOR, "Content-Format: application/cbor")
        assertOptionSerializesTo(ContentFormat(20), "Content-Format: 20")
        assertOptionSerializesTo(Accept(ContentFormat.JSON), "Accept: application/json")
        // some HTTP header-like serializations:
        assertOptionSerializesTo(UriHost("localhost"), "Uri-Host: localhost")
        assertOptionSerializesTo(UriPort(1234), "Uri-Port: 1234")
        assertOptionSerializesTo(ETag("123".encodeToByteArray()), "ETag: 313233")

//        assertOptionSerializesTo(Oscore(byteArrayOf(0x09, 0x14)), "Oscore(value=09 14)")
//        assertOptionSerializesTo(Edhoc, "Edhoc")
//        assertOptionSerializesTo(Block1(100, true, 64), "Block1(blockNumber=100, more=true, blockSize=64)")
    }
}

private fun assertOptionSerializesTo(opt: Option, expected: String) {
    assertEquals(
        actual = Json.encodeToString(OptionSerializer, opt).trim('"'),
        expected = expected,
    )
}

