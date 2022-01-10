package com.example.rally.data.repositories

import com.example.rally.data.Result
import com.example.rally.data.datasources.SavingsDao
import com.example.rally.model.SavingsAccount
import com.example.rally.model.SavingsAccountData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class SavingsRepository @Inject constructor(
    private val savingsDao: SavingsDao
) {

    fun getAllAccounts(): Flow<Result<List<SavingsAccount>>> =
        savingsDao.getAllAccounts()
            .map {
                try {
                    Result.Success(it) as Result<List<SavingsAccount>>
                } catch (e: IOException) {
                    Result.Error(IOException("IO: Could not read data from database"))
                } catch (e: Exception) {
                    Result.Error(Exception("Unknown: Could not read data from database"))
                }
            }

    fun getAllAccountsFake(): Flow<Result<List<SavingsAccount>>> =
        getAllAccountsFake1()
            .map {
                try {
                    Result.Success(it) as Result<List<SavingsAccount>>
                } catch (e: IOException) {
                    Result.Error(IOException("IO: Could not read data from database"))
                } catch (e: Exception) {
                    Result.Error(Exception("Unknown: Could not read data from database"))
                }
            }


    private fun getAllAccountsFake1(): Flow<List<SavingsAccount>> {
        return flowOf(
            listOf(
                SavingsAccount(
                    id = 0,
                    name = "Studiespar",
                    bank = "Nordnet",
                    colorHEX = "1EB980",
                    balance = listOf(
                        SavingsAccountData(
                            OffsetDateTime.now(),
                            100000f
                        ),
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 12,12, 0,0,0,0, ZoneOffset.UTC),
                            93000f
                        ),
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 10,12, 0,0,0,0, ZoneOffset.UTC),
                            73000f
                        ),
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 6,12, 0,0,0,0, ZoneOffset.UTC),
                            53000f
                        ),
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 1,12, 0,0,0,0, ZoneOffset.UTC),
                            21000f
                        ),SavingsAccountData(
                            OffsetDateTime.of(2020, 11,12, 0,0,0,0, ZoneOffset.UTC),
                            18000f
                        ),SavingsAccountData(
                            OffsetDateTime.of(2020, 8,12, 0,0,0,0, ZoneOffset.UTC),
                            15000f
                        ),SavingsAccountData(
                            OffsetDateTime.of(2020, 5,12, 0,0,0,0, ZoneOffset.UTC),
                            10000f
                        ),SavingsAccountData(
                            OffsetDateTime.of(2020, 2,12, 0,0,0,0, ZoneOffset.UTC),
                            9000f
                        ),SavingsAccountData(
                            OffsetDateTime.of(2020, 1,12, 0,0,0,0, ZoneOffset.UTC),
                            6000f
                        ),
                    ).asReversed()
                ),
                SavingsAccount(
                    id = 1,
                    name = "Test2",
                    bank = "Bank2",
                    colorHEX = "FF6859",
                    balance = listOf(
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 12,12, 0,0,0,0, ZoneOffset.UTC),
                            123000f
                        ),
                    )
                ),
                SavingsAccount(
                    id = 2,
                    name = "Test3",
                    bank = "Bank3",
                    colorHEX = "FFCF44",
                    balance = listOf(
                        SavingsAccountData(
                            OffsetDateTime.of(2021, 11,10, 0,0,0,0, ZoneOffset.UTC),
                            9369f
                        )
                    )
                ),
            )
        )
    }

    suspend fun insertAccount(savingsAccount: SavingsAccount) = savingsDao.insert(savingsAccount)

    suspend fun deleteAccount(savingsAccount: SavingsAccount) = savingsDao.delete(savingsAccount)

    suspend fun deleteAccountById(accountId: Int) = savingsDao.deleteById(accountId)

    suspend fun getAccountById(accountId: Int) = savingsDao.getAccountById(accountId)

    fun getAcc(accountId: Int): SavingsAccount {
        return SavingsAccount(
            id = 0,
            name = "Studiespar",
            bank = "Nordnet",
            colorHEX = "1EB980",
            balance = listOf(
                SavingsAccountData(
                    OffsetDateTime.now(),
                    100000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2021, 12, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    93000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2021, 10, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    73000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2021, 6, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    53000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2021, 1, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    21000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2020, 11, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    18000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2020, 8, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    15000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2020, 5, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    10000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2019, 2, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    9000f
                ),
                SavingsAccountData(
                    OffsetDateTime.of(2019, 1, 12, 0, 0, 0, 0, ZoneOffset.UTC),
                    6000f
                ),
            ).asReversed()
        )

    }

    suspend fun updateAccount(savingsAccount: SavingsAccount) = savingsDao.update(savingsAccount)
}
