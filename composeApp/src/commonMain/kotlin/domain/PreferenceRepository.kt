package domain

interface PreferenceRepository {
    suspend fun saveLastUpdatedTime(lastUpdated: String)
    suspend fun isDataFresh(currentTimeStamp:Long) : Boolean
}