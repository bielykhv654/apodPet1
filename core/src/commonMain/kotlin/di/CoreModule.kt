package di

import androidx.room.RoomDatabase
import database.AppDatabase
import database.getDatabaseBuilder
import database.getNasaPhotoDao
import database.getRoomDatabase
import io.ktor.client.HttpClient
import network.AppClient
import network.createHttpClient
import org.koin.dsl.module

val coreModule = module {
    single<AppClient> { AppClient(httpClient = get()) }
    single<HttpClient> { createHttpClient() }
    single { getRoomDatabase(builder = get()) }
    single { getNasaPhotoDao(appDatabase = get()) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }
}
