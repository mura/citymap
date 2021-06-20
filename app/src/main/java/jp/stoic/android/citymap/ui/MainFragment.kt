package jp.stoic.android.citymap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapbox.android.core.permissions.PermissionsManager
import jp.stoic.android.citymap.databinding.FragmentMainBinding
import jp.stoic.android.citymap.viewmodel.CameraViewModel

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private val cameraViewModel: CameraViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.myLocationImageButton?.setOnClickListener {
            val context = context ?: return@setOnClickListener
            if (!PermissionsManager.areLocationPermissionsGranted(context)) {
                return@setOnClickListener
            }

            cameraViewModel.invertTrackingMode()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}