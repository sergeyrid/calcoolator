import retrofit2.Call
import retrofit2.http.*

interface RegistryApi {
    @POST("/api/calculate")
    fun calc(@Body request: CalculationRequest): Call<String>

    @GET("/api/history?limit={N}&before={ID}")
    fun list(): Call<Map<String, Int>>
}


