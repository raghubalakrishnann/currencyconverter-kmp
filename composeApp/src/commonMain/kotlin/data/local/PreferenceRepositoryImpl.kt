package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferenceRepository
import domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferenceRepositoryImpl(private val settings: Settings) : PreferenceRepository{
    companion object{
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"

        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.USD.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.EUR.name
    }
    private val flowSettings :FlowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdatedTime(lastUpdated:String){
        flowSettings.putLong( key =TIMESTAMP_KEY, value = Instant.parse(lastUpdated).toEpochMilliseconds())
    }

    override suspend fun isDataFresh(currentTimeStamp: Long): Boolean {
        val savedTimestamp = flowSettings.getLong(key = TIMESTAMP_KEY, defaultValue = 0L)
        return if(savedTimestamp != 0L){
            val currentTimeInstant = Instant.fromEpochMilliseconds(currentTimeStamp)
            val savedTimeStamp = Instant.fromEpochMilliseconds(savedTimestamp)

            val currentDate = currentTimeInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDate = savedTimeStamp.toLocalDateTime(TimeZone.currentSystemDefault())
            val diff = currentDate.date.daysUntil(savedDate.date)

             (diff < 1)

        } else {
             false
        }
    }

    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(
            key = SOURCE_CURRENCY_KEY ,
            value = code)
    }

    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(
            key = TARGET_CURRENCY_KEY ,
            value = code)
    }

    override suspend fun readSourceCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).map{CurrencyCode.valueOf(it)}
    }

    override suspend fun readTargetCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENCY_KEY,
            defaultValue = DEFAULT_TARGET_CURRENCY
        ).map{CurrencyCode.valueOf(it)}

    }
}