package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.PreferenceRepository
import domain.model.RateStatus
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

    init{
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun sendEvent(event: HomeUIEvent){
        when(event){
            HomeUIEvent.RefreshRates -> {
                screenModelScope.launch {
                    getRateStatus()
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