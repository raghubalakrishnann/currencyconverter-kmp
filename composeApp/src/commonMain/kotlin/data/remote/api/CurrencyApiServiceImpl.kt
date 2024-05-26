package data.remote.api

import domain.CurrencyApiService
import domain.model.Currency
import domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl : CurrencyApiService {
    companion object{
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "XXXX"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            println("getLatestExchangeRates response: ${response.body<String>()}")
            if(response.status.value == 200){
                val currencies = response.body<List<Currency>>()
                RequestState.Success(currencies)

            }else{
                RequestState.Error(error="Http Error Code : ${response.status.value}"  )
            }
        }catch (e:Exception){
            RequestState.Error(error = e.message.toString())
        }
    }
}