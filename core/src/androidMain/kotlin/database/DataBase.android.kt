package database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context: Context by inject(Context::class.java)
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}