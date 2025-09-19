package cis3334.kotlin_heartrate_roomdb

// =============================================
// Heartrate.kt — Room Entity (Kotlin data class)
// =============================================
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heartrate")
data class Heartrate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val pulse: Int,
    val age: Int,
    val timestamp: Long = System.currentTimeMillis()
) {

    // ----- Derived values (not stored) -----

    /** CDC-style simple estimate */
    val maxHeartRate: Double
        get() = 220.0 - age

    /** Fraction of max (0.0..1.0). Use Double to avoid integer division. */
    val percentOfMax: Double
        get() = if (maxHeartRate > 0) pulse / maxHeartRate else 0.0

    /** Which range this heart rate falls into (index into bounds/names/descriptions). */
    val rangeIndex: Int
        get() {
            val p = percentOfMax
            for (i in RANGE_BOUNDS.indices) {
                if (p < RANGE_BOUNDS[i]) return i
            }
            // If somehow above all bounds, clamp to last
            return RANGE_BOUNDS.lastIndex
        }

    // ----- Helpers matching your Java API -----

    fun rangeName(): String = RANGE_NAMES[rangeIndex]

    fun rangeDescription(): String = RANGE_DESCRIPTIONS[rangeIndex]

    override fun toString(): String =
        "Pulse of $pulse - Cardio level: ${rangeName()}"

    companion object {
        // Same semantics as your Java arrays
        private val RANGE_NAMES = arrayOf(
            "Resting", "Moderate", "Endurance", "Aerobic", "Anaerobic", "Red zone"
        )

        private val RANGE_DESCRIPTIONS = arrayOf(
            "In active or resting",
            "Weight maintenance and warm up",
            "Fitness and fat burning",
            "Cardio training and endurance",
            "Hardcore interval training",
            "Maximum Effort"
        )

        // Upper bounds (exclusive) for each bucket, as fractions of max HR
        private val RANGE_BOUNDS = doubleArrayOf(0.50, 0.60, 0.70, 0.80, 0.90, 1.00)
    }
}
