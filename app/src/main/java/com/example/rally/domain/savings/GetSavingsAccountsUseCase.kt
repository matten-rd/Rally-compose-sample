package com.example.rally.domain.savings

import com.example.rally.data.repositories.SavingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetSavingsAccountsUseCase(
    private val savingsRepository: SavingsRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
//
//    suspend operator fun invoke(): List<SavingsData> =
//        withContext(defaultDispatcher) {
//            val savingsAccounts = savingsRepository.fetchSavingsAccounts()
//        }
}