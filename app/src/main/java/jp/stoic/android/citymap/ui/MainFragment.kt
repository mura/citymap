package jp.stoic.android.citymap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapbox.android.core.permissions.PermissionsManager
import jp.stoic.android.citymap.R
import jp.stoic.android.citymap.viewmodel.CameraViewModel
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment : Fragment() {
    private val cameraViewModel: CameraViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        view.myLocationImageButton.setOnClickListener {
            val context = context ?: return@setOnClickListener
            if (!PermissionsManager.areLocationPermissionsGranted(context)) {
                return@setOnClickListener
            }

            cameraViewModel.invertTrackingMode()
        }

        return view
    }
}