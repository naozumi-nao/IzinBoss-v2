package com.naozumi.izinboss.prototype_features.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.naozumi.izinboss.prototype_features.source.local.entity.LeaveRequestEntity

@Database(entities = [LeaveRequestEntity::class], version = 1, exportSchema = false)
abstract class LeaveRequestDatabase : RoomDatabase() {
    abstract fun leaveRequestDao(): LeaveRequestDao

    companion object {
        @Volatile
        private var instance: LeaveRequestDatabase? = null

        fun getInstance(context: Context): LeaveRequestDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LeaveRequestDatabase::class.java, "LeaveRequests.db"
                ).fallbackToDestructiveMigration().build()
            }
    }
}