package domain.model

sealed class RequestState<out T> {

    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val error: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isSuccess() = this is Success

    fun getSuccessData() = (this as Success).data
    fun getErrorMessage():String = (this as Error).getErrorMessage()

}