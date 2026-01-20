package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.serialize.UUIDSerializer
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UUIDSerializerTest {
    @Test
    fun `test uuid serialization`() {
        val uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val json = Json.encodeToString(UUIDSerializer, uuid)
        assertEquals("\"550e8400-e29b-41d4-a716-446655440000\"", json)
    }
}
