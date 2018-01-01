package de.jbamberger.api.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import de.jbamberger.api.data.FeedItem


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Dao
interface FeedItemDao {
    @Query("SELECT * FROM feedItems")
    fun getAll(): LiveData<List<FeedItem>>

//    @Query("SELECT * FROM feedItems WHERE id IN (:ids)")
//    fun loadAllByIds(ids: IntArray): List<FeedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg feedItems: FeedItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feedItems: List<FeedItem>)

    @Delete
    fun delete(user: FeedItem)

}