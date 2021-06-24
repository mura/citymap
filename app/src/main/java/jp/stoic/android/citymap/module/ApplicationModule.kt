package jp.stoic.android.citymap.module

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.stoic.android.citymap.repository.AssetRepository
import jp.stoic.android.citymap.room.HistoryDatabase

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun historyDatabase(app: Application) =
        Room.databaseBuilder(app, HistoryDatabase::class.java, "history").build()

    @Provides
    fun assetRepository(app: Application) = AssetRepository(app)
}