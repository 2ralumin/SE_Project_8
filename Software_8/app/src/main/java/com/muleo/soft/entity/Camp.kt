package com.muleo.soft.entity

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "camp_table")
data class Camp(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "isEmpty")
    val isEmpty: Boolean,
    @ColumnInfo(name = "imgPath")
    val imgPath: String,
    @ColumnInfo(name = "accountNumber")
    val accountNumber: String,
    @ColumnInfo(name = "inf")
    val inf: String,
)


@Dao
interface CampDao {

    @Query("SELECT * FROM camp_table WHERE id = :campId")
    suspend fun get(campId: Int): Camp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdate(vararg camps: Camp)

    @Query("SELECT * FROM camp_table ORDER BY id ASC")
    fun getCampList(): Flow<List<Camp>>

    @Delete
    fun deleteCamps(vararg camps: Camp)
}

class CampRepository(private val campDao: CampDao) {

    val allCamps: Flow<List<Camp>> = campDao.getCampList()

    suspend fun addOrUpdate(vararg camps: Camp) {
        campDao.addOrUpdate(camps = camps)
    }

    suspend fun get(campId: Int): Camp? {
        return campDao.get(campId = campId)
    }
}

