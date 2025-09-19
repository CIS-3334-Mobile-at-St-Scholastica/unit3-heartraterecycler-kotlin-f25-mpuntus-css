package cis3334.kotlin_heartrate_roomdb

// =============================================
// HeartrateRepository.kt — simple repository
// =============================================
class HeartrateRepository(private val dao: HeartrateDao) {
    val all: kotlinx.coroutines.flow.Flow<List<Heartrate>> = dao.getAll()
    suspend fun add(pulse: Int, age: Int) = dao.insert(Heartrate(pulse = pulse, age = age))
    suspend fun clear() = dao.clear()
}