@Dao
interface CreditCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCard(creditCard: CreditCard): Long

    @Query("SELECT * FROM credit_cards WHERE userId = :userId")
// ...existing code...

