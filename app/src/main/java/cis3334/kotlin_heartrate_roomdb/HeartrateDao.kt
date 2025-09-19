package cis3334.kotlin_heartrate_roomdb

// =============================================
// HeartrateDao.kt — DAO interface
// =============================================

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface HeartrateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Heartrate)


    @Query("SELECT * FROM heartrate ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Heartrate>>


    @Query("DELETE FROM heartrate")
    suspend fun clear()
}