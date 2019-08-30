package com.pingone.loginapp.repository.datasource.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Single

@Entity(tableName = "token")
data class RoomToken(

    @ColumnInfo(name = "access_token") @PrimaryKey val accessToken: String,
    @ColumnInfo(name = "token_type") val tokenType: String,
    @ColumnInfo(name = "expires_in") val expiresIn: String,
    @ColumnInfo(name = "scope") val scope: String,
    @ColumnInfo(name = "id_token") val idToken: String
)

@Dao
interface TokenDAO {

    @Query("SELECT * FROM token LIMIT 1")
    fun getToken(): Single<RoomToken>

    @Insert(onConflict = REPLACE)
    fun insertToken(token: RoomToken)

    @Query("DELETE from token")
    fun deleteAll()
}

@Database(entities = [RoomToken::class], version = 1)
abstract class TokenDatabase : RoomDatabase() {

    abstract fun tokenDAO(): TokenDAO

}