package jp.stoic.android.citymap.module

import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import jp.stoic.android.citymap.domain.Analytics

@Module
@InstallIn(ActivityComponent::class)
object MainActivityModule {

    @Provides
    fun analytics(firebaseAnalytics: FirebaseAnalytics) = Analytics(firebaseAnalytics)
}