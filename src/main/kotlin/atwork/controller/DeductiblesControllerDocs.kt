package atwork.controller

import atwork.controller.responses.Deductible
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.context.annotation.Profile

@Profile("!prod")
@Operation(summary = "Provided a JWT, get an array of Deductibles")
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [
                Content(
                    mediaType = "application/json",
                    array = ArraySchema(schema = Schema(implementation = Deductible::class))
                )
            ]
        ),
        ApiResponse(responseCode = "401", description = "Unauthorized", content = [Content()]),
        ApiResponse(responseCode = "403", description = "Forbidden", content = [Content()]),
        ApiResponse(responseCode = "404", description = "Not Found", content = [Content()])
    ]
)
annotation class DeductiblesControllerDocs
