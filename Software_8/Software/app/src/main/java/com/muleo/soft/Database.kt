package com.muleo.soft

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.muleo.soft.entity.Camp
import com.muleo.soft.entity.CampDao
import com.muleo.soft.entity.User
import com.muleo.soft.entity.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.room.Database as DB


@DB(entities = [User::class, Camp::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun campDao(): CampDao

    private class UserCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populate(database.userDao())
                }
            }
        }


        suspend fun populate(userDao: UserDao) {
            // 초기화
//            userDao.deleteAll() // 유저 초기화
//            userDao.add( // 관리자 계정 추가
//                User(
//                    id = "Manager",
//                    pw = "Manager",
//                    age = 0,
//                    email = "manager@naver.com",
//                    phone = "01000000000"
//                )
//            )
            // TODO
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): Database =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "database"
                )
                    .addCallback(UserCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
    }
}