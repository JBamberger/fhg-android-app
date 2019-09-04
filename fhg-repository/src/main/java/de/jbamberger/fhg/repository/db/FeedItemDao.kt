package de.jbamberger.fhg.repository.db

import androidx.lifecycle.LiveData
import androidx.room.*
import de.jbamberger.fhg.repository.data.FeedItem


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Dao
internal interface FeedItemDao {
    @Query("SELECT * FROM feedItems ORDER BY date DESC")
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