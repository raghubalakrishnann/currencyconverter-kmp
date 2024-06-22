package di

import com.russhwolf.settings.Settings
import data.local.PreferenceRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.CurrencyApiService
import domain.PreferenceRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.HomeViewModel


val appModule = module {
    single{ Settings() }
    single<PreferenceRepository> { PreferenceRepositoryImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get())}

    factory {
        HomeViewModel(
            preferences = get(),
            api = get()
        )
    }

}

fun initializeKoin(){
    startKoin {
        modules(appModule)
    }
}