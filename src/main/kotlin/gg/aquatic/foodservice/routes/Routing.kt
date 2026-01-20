package gg.aquatic.foodservice.routes

import gg.aquatic.foodservice.data.repository.BrandRepository
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.then
import org.http4k.format.Jackson
import org.http4k.routing.routes

/**
 * Modular routing system for potential future expansion with additional routes.
 */
fun appRouter(brandRepository: BrandRepository): HttpHandler {
    val apiContract = contract {
        // Have had issues with using KotlinxSerialization as format, switched to Jackson
        renderer = OpenApi3(ApiInfo("Brand Service API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += brandRoutes(brandRepository)
    }

    return catchAllExceptionFilter()
        .then(
            routes(
                apiContract
            )
        )
}

private fun catchAllExceptionFilter() = Filter { next ->
    { req ->
        try {
            next(req)
        } catch (e: Exception) {
            Response(INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body("""{"error": "${e.message ?: "Unknown error"}"}""")
        }
    }
}
