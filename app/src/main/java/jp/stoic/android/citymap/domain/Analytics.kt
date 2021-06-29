package jp.stoic.android.citymap.domain

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import jp.stoic.android.citymap.vo.SelectedShape
import javax.inject.Inject

class Analytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun clickShape(selectedShape: SelectedShape) {
        val bundle = bundleOf(
            FirebaseAnalytics.Param.ITEM_ID to selectedShape.code,
            FirebaseAnalytics.Param.ITEM_NAME to selectedShape.name
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
}