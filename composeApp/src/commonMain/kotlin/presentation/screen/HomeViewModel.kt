package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.MongoRepository
import domain.PreferenceRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUIEvent {
    data object RefreshRates : HomeUIEvent()
    data object SwitchCurrency : HomeUIEvent()
}
class HomeViewModel (
    private val preferences : PreferenceRepository,
    private val mongoDB: MongoRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> =
        mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init{
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    fun sendEvent(event: HomeUIEvent){
        when(event){
            HomeUIEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
            HomeUIEvent.SwitchCurrency -> {
                screenModelScope.launch {
                    switchCurrency()
                }
            }
        }
    }

    private fun readSourceCurrency(){
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if(selectedCurrency != null){
                    _sourceCurrency.value = RequestState.Success(selectedCurrency)
                }else{
                    _sourceCurrency.value = RequestState.Error(error = "Currency not found")
                }

            }
        }
    }

    private fun readTargetCurrency(){
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(selectedCurrency)
                }
            }
        }
    }

    private suspend fun fetchNewRates(){
        try{
            val localCache = mongoDB.readCurrencyData().first()
            if(localCache.isSuccess()){
                if(localCache.getSuccessData().isNotEmpty()){
                    println("fetchNewRates localCache: $localCache")
                    _allCurrencies.addAll(localCache.getSuccessData())
                    if(!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())){
                        println("HomeViewModel DATA NOT FRESH")
                        cacheTheData()
                    }else{
                        println("HomeViewModel DATA is FRESH")
                    }
                }else{
                    println("HomeViewModel DATABASE needs DATA")
                    cacheTheData()
                }

            }else if(localCache.isError()){
                println("HomeViewModel Error reading local database ")
            }
            getRateStatus()
        }catch (e:Exception){
            println()
        }
    }

    private suspend fun cacheTheData(){
        val fetchedData = api.getLatestExchangeRates()
        if(fetchedData.isSuccess()){
            println("HomeViewModel fetch success")
            mongoDB.cleanUp()
            fetchedData.getSuccessData().forEach {
                println("HomeViewModel adding code :${it.code} value:${it.value}")
                mongoDB.insertCurrencyData(it)
            }
            _allCurrencies.addAll(fetchedData.getSuccessData())
        }else if(fetchedData.isError()) {
            println("HomeViewModel fetch failed")
        }

    }

    private suspend fun getRateStatus(){
        _rateStatus.value = if(preferences.isDataFresh(
            currentTimeStamp = Clock.System.now().toEpochMilliseconds()
        ) ) RateStatus.Fresh else RateStatus.Stale
    }

    private fun switchCurrency(){
        val sourceCurrency = _sourceCurrency.value
        val targetCurrency = _targetCurrency.value
        _sourceCurrency.value = targetCurrency
        _targetCurrency.value = sourceCurrency

    }


}