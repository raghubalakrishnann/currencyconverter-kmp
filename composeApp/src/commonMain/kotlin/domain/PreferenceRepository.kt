package domain

import domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    suspend fun saveLastUpdatedTime(lastUpdated: String)
    suspend fun isDataFresh(currentTimeStamp:Long) : Boolean
    suspend fun saveSourceCurrencyCode(code: String)
    suspend fun saveTargetCurrencyCode(code: String)
    suspend fun readSourceCurrencyCode(): Flow<CurrencyCode>
    suspend fun readTargetCurrencyCode(): Flow<CurrencyCode>

}