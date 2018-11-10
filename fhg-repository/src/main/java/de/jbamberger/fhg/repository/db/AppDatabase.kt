package de.jbamberger.fhg.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import de.jbamberger.fhg.repository.data.FeedItem
import de.jbamberger.fhg.repository.data.dao.FeedItemDao

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Database(entities = [FeedItem::class], version = 1)
internal abstract class AppDatabase: RoomDatabase() {
    abstract val feedItemDao: FeedItemDao
}