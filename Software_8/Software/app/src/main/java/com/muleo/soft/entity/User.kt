package com.muleo.soft.entity

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pw")
    val pw: String,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {

    // User 하나를 얻기
    @Query("SELECT * From user_table WHERE id = :userId")
    suspend fun get(userId: String): User?

    // User 추가 및 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(vararg users: User)

    // Room 쿼리가 FLow를 반환하면 자동으로 백그라운드 스레드에서 비동기식으로 실행됩니다.
    @Query("SELECT * FROM user_table ORDER BY date ASC")
    fun getUserList(): Flow<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

}

// 전체 db 가 아닌 필요한 dao 만 속성으로 등록.
class UserRepository(private val userDao: UserDao) {

    // data 가 바뀌면 Flow 가 observer 에게 notify
    val allUsers: Flow<List<User>> = userDao.getUserList()

    suspend fun add(vararg users: User) {
        userDao.add(users = users)
    }

    suspend fun get(id: String): User? {
        return userDao.get(userId = id)
    }

}
