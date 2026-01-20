package gg.aquatic.foodservice.data.model

import gg.aquatic.foodservice.data.serialize.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlinx.datetime.LocalDateTime

@Serializable
data class Brand(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val externalId: String?,
    val lastUpdated: LocalDateTime?
)