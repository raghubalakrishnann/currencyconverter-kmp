package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.PreferenceRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUIEvent {
    data object RefreshRates : HomeUIEvent()
}
class HomeViewModel (
    private val preferences : PreferenceRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> =
        mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus
    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init{
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun sendEvent(event: HomeUIEvent){
        when(event){
            HomeUIEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
        }
    }
    private suspend fun fetchNewRates(){
        try{
            api.getLatestExchangeRates()
            getRateStatus()
        }catch (e:Exception){
            println()
        }
    }

    private suspend fun getRateStatus(){
        _rateStatus.value = if(preferences.isDataFresh(
            currentTimeStamp = Clock.System.now().toEpochMilliseconds()
        ) ) RateStatus.Fresh else RateStatus.Stale
    }


}