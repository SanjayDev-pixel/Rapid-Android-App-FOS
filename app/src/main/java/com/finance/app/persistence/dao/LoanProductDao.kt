package com.finance.app.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finance.app.persistence.model.LoanProductMaster

@Dao
interface LoanProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoanProduct(product: LoanProductMaster)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoanProductList(product: ArrayList<LoanProductMaster>)

    @Query("SELECT * FROM LoanProductMaster")
    fun getAllLoanProduct(): LiveData<List<LoanProductMaster>?>

    @Query("SELECT * FROM LoanProductMaster WHERE productID= :productId LIMIT 1")
    fun getLoanProductWithId(productId: Int): LiveData<LoanProductMaster?>

    @Query("DELETE FROM LoanProductMaster")
    fun deleteLoanProduct()

}