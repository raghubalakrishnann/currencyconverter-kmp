package presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.RateStatus
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.exchange_illustration
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.displayCurrentDateTime
import ui.theme.headerColor


@Composable
fun HomeHeader(
    status:RateStatus,
    onRatesRefresh : () -> Unit)
{
        Column (modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(all=24.dp))
        {
            Spacer(modifier = Modifier.height(24.dp))
            RatesStatus(
                status = status,
                onRatesRefresh = onRatesRefresh
            )
        }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun RatesStatus(
    status: RateStatus,
    onRatesRefresh : () -> Unit)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Row{
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.exchange_illustration),
                contentDescription = "Exchange Rate Illustration"
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = displayCurrentDateTime(),
                    color = Color.White,
                )

                Text(
                    text = status.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = status.color
                )
            }
        }
    }

}