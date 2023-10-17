package com.naozumi.izinboss.prototype_features.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.naozumi.izinboss.prototype_features.source.local.entity.CompanyEntity

@Database(entities = [CompanyEntity::class], version = 1, exportSchema = false)
abstract class CompanyDatabase : RoomDatabase() {
    abstract fun companyDao() : CompanyDao

    companion object {
        @Volatile
        private var instance: CompanyDatabase? = null
        fun getInstance(context: Context): CompanyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CompanyDatabase::class.java, "Company.db"
                ).fallbackToDestructiveMigration().build()
            }
    }
}