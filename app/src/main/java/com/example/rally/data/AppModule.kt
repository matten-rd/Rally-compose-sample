package com.example.rally.data

import android.app.Application
import androidx.room.Room
import com.example.rally.data.databases.RallyRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, RallyRoomDatabase::class.java, "rally_roomDB_v2")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideSavingsDao(db: RallyRoomDatabase) = db.savingsDao()

}