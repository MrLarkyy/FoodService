package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.Config
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.routes.appRouter
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.ApplicationLoadBalancerLambdaFunction
import org.http4k.core.Filter
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("gg.aquatic.foodservice.Main")

val loggingFilter = Filter { next ->
    { request ->
        val start = System.currentTimeMillis()
        val response = next(request)
        val latency = System.currentTimeMillis() - start
        logger.info("${request.method} ${request.uri} returned ${response.status} in ${latency}ms")
        response
    }
}

private val appHandler by lazy {
    logger.info("Initializing application handler...")
    val repository = BrandRepository(Config.db)

    ServerFilters.CatchAll()
        .then(loggingFilter)
        .then(appRouter(repository))

    appRouter(repository)
}
// 1. For Local Execution
fun main() {
    println("Starting server on port 8080...")
    appHandler.asServer(Netty(8080)).start().block()
}

// 2. For AWS Lambda Execution
@Suppress("unused")
class LambdaHandler : ApiGatewayV2LambdaFunction(appHandler)

// 3. For AWS Lambda Execution (Application Load Balancer)
@Suppress("unused")
class AlbLambdaHandler : ApplicationLoadBalancerLambdaFunction(appHandler)