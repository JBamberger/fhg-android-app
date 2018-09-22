package de.jbamberger.fhg.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import de.jbamberger.fhg.repository.data.FeedItem

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@Database(entities = [FeedItem::class], version = 1)
internal abstract class AppDatabase: RoomDatabase() {
    abstract val feedItemDao: FeedItemDao
}