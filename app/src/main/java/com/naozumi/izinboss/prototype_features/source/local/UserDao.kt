package com.naozumi.izinboss.prototype_features.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.naozumi.izinboss.prototype_features.source.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE uid = :uid")
    fun getUser(uid: String): Flow<UserEntity>

    @Query("SELECT * FROM UserEntity WHERE companyId = :companyId")
    fun getUsersWithSameCompany(companyId: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}