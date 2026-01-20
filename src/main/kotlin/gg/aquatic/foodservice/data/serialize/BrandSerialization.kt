package gg.aquatic.foodservice.data.serialize

import kotlinx.serialization.json.Json
import org.http4k.format.ConfigurableKotlinxSerialization
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings

object BrandSerialization : ConfigurableKotlinxSerialization(
    {
        Json {
            ignoreUnknownKeys = true
            asConfigurable().withStandardMappings().done()
        }
    }
)