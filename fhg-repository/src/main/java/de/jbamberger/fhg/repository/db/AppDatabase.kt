package de.jbamberger.fhg.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import de.jbamberger.api.data.FeedItem
import de.jbamberger.api.data.dao.FeedItemDao

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Database(entities = [FeedItem::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract val feedItemDao: FeedItemDao
}