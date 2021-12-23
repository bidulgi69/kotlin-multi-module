rootProject.name = "multi-module"

include(
    ":api",
    ":utils",
    ":microservices:services:order-service"
)