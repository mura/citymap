package jp.stoic.android.citymap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import jp.stoic.android.citymap.databinding.FragmentHistoryBinding
import jp.stoic.android.citymap.viewmodel.HistoryViewModel

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [HistoryFragment.OnListFragmentInteractionListener] interface.
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var binding: FragmentHistoryBinding? = null
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding!!.root

        // Set the adapter
        binding?.list?.also { list ->
            list.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            historyViewModel.history.observe(viewLifecycleOwner, {
                list.adapter = HistoryRecyclerViewAdapter(it)
            })
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
