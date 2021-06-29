package jp.stoic.android.citymap.module

import android.app.Application
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.stoic.android.citymap.repository.AssetRepository
import jp.stoic.android.citymap.repository.BoundsRepository
import jp.stoic.android.citymap.repository.HistoryRepository
import jp.stoic.android.citymap.room.HistoryDatabase

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun historyDatabase(app: Application) =
        Room.databaseBuilder(app, HistoryDatabase::class.java, "history").build()

    @Provides
    fun assetRepository(app: Application) = AssetRepository(app)

    @Provides
    fun boundsRepository(assetRepository: AssetRepository) = BoundsRepository(assetRepository)

    @Provides
    fun historyRepository(db: HistoryDatabase) = HistoryRepository(db)

    @Provides
    fun firebaseAnalytics(app: Application) = FirebaseAnalytics.getInstance(app)
}