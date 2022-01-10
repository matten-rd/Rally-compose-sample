package com.example.rally.data.datasources

import androidx.room.*
import com.example.rally.model.SavingsAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Query("SELECT * FROM savings_table ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<SavingsAccount>>

    @Query("SELECT * FROM savings_table WHERE id = :id")
    suspend fun getAccountById(id: Int): SavingsAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savingsAccount: SavingsAccount)

    @Update
    suspend fun update(savingsAccount: SavingsAccount)

    @Delete
    suspend fun delete(savingsAccount: SavingsAccount)

    @Query("DELETE FROM savings_table WHERE id = :id")
    suspend fun deleteById(id: Int)

}