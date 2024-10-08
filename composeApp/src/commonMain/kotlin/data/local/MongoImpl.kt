package data.local

import domain.MongoRepository
import domain.model.Currency
import domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoImpl : MongoRepository {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if(realm == null || realm?.isClosed() == true){
           val config = RealmConfiguration.Builder(
               schema = setOf(Currency::class)
           ).compactOnLaunch().build()
            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: Currency) {
        realm?.write {
            copyToRealm(currency)
        }
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
       return realm?.query<Currency>()
        ?.asFlow()
        ?.map{result ->
            RequestState.Success(result.list)
        }
           ?:flow{
               RequestState.Error(error = "Realm is not initialized")
           }
    }

    override suspend fun cleanUp() {
        realm?.write {
            val currencies = this.query<Currency>()
            delete(currencies)
        }
    }
}