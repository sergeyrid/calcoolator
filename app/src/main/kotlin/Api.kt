import retrofit2.Call
import retrofit2.http.*

interface RegistryApi {
    @POST("/api/calculate")
    //fun calc(@Body request: CalculationRequest): Call<String>

    @GET("/api/history")
    fun list(): Call<Map<Long, Long>>
}

