package gg.aquatic.foodservice.routes

import gg.aquatic.foodservice.data.model.Brand
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.data.serialize.BrandSerialization
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Query
import org.http4k.lens.string

private val nameQuery = Query.string().optional("name", "Filter by brand name (partial match, case insensitive)")
private val externalIdQuery = Query.string().optional("externalId", "Filter by exact external ID")
private val sortByQuery = Query.string().optional("sortBy", "Field to sort by (name, last_updated)")
private val sortDirQuery = Query.string().optional("sortDirection", "Sort direction (asc, desc)")

private val brandsLens = BrandSerialization.autoBody<List<Brand>>().toLens()

fun brandRoutes(repository: BrandRepository): List<ContractRoute> = listOf(
    "/brands" meta {
        summary = "Get brands"
        description = "Retrieves a filtered and sorted list of brands from the database"
        queries += nameQuery
        queries += externalIdQuery
        queries += sortByQuery
        queries += sortDirQuery
        returning(OK, brandsLens to listOf(), "List of matching brands")
    } bindContract GET to { req: Request ->
        val brands = repository.findBrands(
            name = nameQuery(req),
            externalId = externalIdQuery(req),
            sortBy = sortByQuery(req),
            sortDirection = sortDirQuery(req)
        )
        Response(OK).with(brandsLens of brands)
    }
)