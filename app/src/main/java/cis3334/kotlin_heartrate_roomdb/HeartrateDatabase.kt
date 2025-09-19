package cis3334.kotlin_heartrate_roomdb

// =============================================
// HeartrateDatabase.kt — RoomDatabase
// =============================================
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Heartrate::class], version = 1, exportSchema = false)
abstract class HeartrateDatabase : RoomDatabase() {
    abstract fun dao(): HeartrateDao


    companion object {
        @Volatile private var INSTANCE: HeartrateDatabase? = null


        fun get(context: Context): HeartrateDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                HeartrateDatabase::class.java,
                "heartrate.db"
            ).build().also { INSTANCE = it }
        }
    }
}