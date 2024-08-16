package presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.model.Currency
import domain.model.CurrencyCode
import domain.model.RateStatus
import domain.model.RequestState
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.exchange_illustration
import kotlinproject.composeapp.generated.resources.refresh_ic
import kotlinproject.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import util.displayCurrentDateTime
import ui.theme.headerColor
import ui.theme.staleColor


@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double = 1.0,
    onAmountChange: (Double) -> Unit = {},
    onRatesRefresh: () -> Unit,
    onSwitchClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(all = 24.dp)
    )
    {
        Spacer(modifier = Modifier.height(24.dp))
        RatesStatus(
            status = status,
            onRatesRefresh = onRatesRefresh
        )
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClick = onSwitchClick)
        Spacer(modifier = Modifier.height(24.dp))
        AmountInput(
            amount = amount,
            onAmountChange = onAmountChange )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick : () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrencyView(
            placeHolder = "from",
            currency = source,
            onClick = {
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        IconButton(
            modifier = Modifier.padding(24.dp),
            onClick = onSwitchClick
        ) {
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        CurrencyView(
            placeHolder = "to",
            currency = target,
            onClick = {
            })

    }
}



@OptIn(ExperimentalResourceApi::class)
@Composable
fun RatesStatus(
    status: RateStatus,
    onRatesRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row {
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

        if(status == RateStatus.Stale){
            IconButton(onClick = onRatesRefresh){
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh_ic),
                    contentDescription = "Refresh",
                    tint = staleColor
                )
            }
        }
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RowScope.CurrencyView(
    placeHolder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeHolder,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .clickable { onClick() }
                .padding(all = 12.dp)
                .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
            if(currency.isSuccess()){
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(CurrencyCode.valueOf(currency.getSuccessData().code).flag),
                    contentDescription = "Country Flag",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = CurrencyCode.valueOf(currency.getSuccessData().code).name,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color.White)

            }

        }
    }
}


@Composable
fun AmountInput(
    amount: Double,
    onAmountChange: (Double) -> Unit
){
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .animateContentSize()
            .height(54.dp),
        value = "$amount",
        onValueChange = {
            onAmountChange(it.toDouble())
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor =  Color.White.copy(alpha = 0.05f),
            errorContainerColor = Color.White.copy(alpha = 0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor =Color.Transparent,
            cursorColor = Color.White),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
    )
}
