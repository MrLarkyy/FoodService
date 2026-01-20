package gg.aquatic.foodservice

import gg.aquatic.foodservice.data.Config
import gg.aquatic.foodservice.data.repository.BrandRepository
import gg.aquatic.foodservice.routes.appRouter
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.serverless.ApplicationLoadBalancerLambdaFunction

private val appHandler by lazy {
    val repository = BrandRepository(Config.db)
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