package jp.stoic.android.citymap.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import jp.stoic.android.citymap.R
import jp.stoic.android.citymap.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_history_list.view.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [HistoryFragment.OnListFragmentInteractionListener] interface.
 */
class HistoryFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_list, container, false)

        // Set the adapter
        with(view.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            historyViewModel.history.observe(this@HistoryFragment, Observer {
                adapter = HistoryRecyclerViewAdapter(it)
            })
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.show()
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.hide()
        }
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
