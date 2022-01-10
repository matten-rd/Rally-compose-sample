package com.example.rally.model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Entity(tableName = "savings_table")
@Parcelize
data class SavingsAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val bank: String,
    val colorHEX: String,
    val balance: List<SavingsAccountData>
) : Parcelable

@Parcelize
@Serializable
data class SavingsAccountData(
    @Serializable(KOffsetDateTimeSerializer::class)
    val date: OffsetDateTime,
    val amount: Float
) : Parcelable
