package domain.model

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

sealed class RequestState<out T> {

    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val error: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isSuccess() = this is Success

    fun getSuccessData() = (this as Success).data
    fun getErrorMessage(): String = (this as Error).getErrorMessage()

}

@Composable
fun <T> RequestState<T>.DisplayResult(
    onIdle: (@Composable () -> Unit)? = null,
    onError: (@Composable (String) -> Unit)? = null,
    onLoading: (@Composable () -> Unit)? = null,
    onSuccess: @Composable (T) -> Unit,
    transitionSpec: ContentTransform = scaleIn(tween(durationMillis = 500))
            + fadeIn(tween(durationMillis = 800))
            togetherWith scaleOut(tween(durationMillis = 400))
            + fadeOut(tween(durationMillis = 800))

) {
  AnimatedContent(
      targetState = this,
      transitionSpec = { transitionSpec },
      label ="  Content Animation"
  ){ state ->
      Row (modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
      ){
          when (state) {
              is RequestState.Idle -> {
                  onIdle?.invoke()
              }
              is RequestState.Loading -> {
                  onLoading?.invoke()
              }
              is RequestState.Success -> {
                  onSuccess(state.getSuccessData())
              }
              is RequestState.Error -> {
                  onError?.invoke(state.getErrorMessage())
              }
          }

          }

  }

}