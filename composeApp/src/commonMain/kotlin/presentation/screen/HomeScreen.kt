package presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import data.remote.api.CurrencyApiServiceImpl
import domain.model.CurrencyType
import presentation.component.CurrencyPickerDialog
import presentation.component.HomeBody
import presentation.component.HomeHeader
import ui.theme.surfaceColor

class HomeScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val allCurrencies = viewModel.allCurrencies
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        var amount by rememberSaveable() { mutableStateOf(0.0) }
        var selectedCurrencyCode :CurrencyType by remember {
            mutableStateOf(CurrencyType.None)
        }
        var dialogOpened by remember { mutableStateOf(false) }

        if(dialogOpened && selectedCurrencyCode != CurrencyType.None){

            println("getLatestExchangeRates CurrencyPickerDialog 11 currencies: ${allCurrencies.size}")
            allCurrencies.forEach {
                println("getLatestExchangeRates CurrencyPickerDialog 12 code: ${it.code}")
            }

            CurrencyPickerDialog(
                currencies = allCurrencies,
                currencyType = selectedCurrencyCode,
                onConfirmClick = { currencyCode ->
                    if(selectedCurrencyCode is CurrencyType.Source){
                        viewModel.sendEvent(
                            HomeUIEvent.SaveSourceCurrencyCode(code = currencyCode.name)
                        )
                    }else if(selectedCurrencyCode is CurrencyType.Target){
                        viewModel.sendEvent(
                            HomeUIEvent.SaveTargetCurrencyCode(code = currencyCode.name)
                        )
                    }
                    selectedCurrencyCode = CurrencyType.None
                    dialogOpened = false
                },
                onDismiss = {
                    selectedCurrencyCode = CurrencyType.None
                    dialogOpened = false
                })
        }


        Column(
            modifier = Modifier.fillMaxSize()
                .background(surfaceColor)
        ) {
            HomeHeader(
                status = rateStatus,
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount,
                onAmountChange = {
                    amount = it
                },
                onSwitchClick = {
                    viewModel.sendEvent(
                        HomeUIEvent.SwitchCurrency
                    )
                },
                onRatesRefresh = {
                    viewModel.sendEvent(
                        HomeUIEvent.RefreshRates
                    )
                },
                onCurrencyTypeSelect = { currencyType ->
                    selectedCurrencyCode = currencyType
                    dialogOpened = true

                })

            HomeBody(
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount
            )

        }
    }
}