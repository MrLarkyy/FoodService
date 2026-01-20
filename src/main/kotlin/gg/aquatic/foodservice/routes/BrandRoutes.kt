package gg.aquatic.foodservice.routes

import gg.aquatic.foodservice.data.model.Brand
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.data.serialize.BrandSerialization
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Query
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

fun brandRoutes(repository: BrandRepository): RoutingHttpHandler {
    val nameLens = Query.string().optional("name")
    val externalIdLens = Query.string().optional("externalId")
    val sortByLens = Query.string().optional("sortBy")
    val sortDirectionLens = Query.string().optional("sortDirection")
    val brandsLens = BrandSerialization.autoBody<List<Brand>>().toLens()

    return "/brands" bind routes(
        "/" bind GET to { req ->
            val brands = repository.findBrands(
                name = nameLens(req),
                externalId = externalIdLens(req),
                sortBy = sortByLens(req),
                sortDirection = sortDirectionLens(req)
            )
            brandsLens.inject(brands, Response(OK))
        }
    )
}
