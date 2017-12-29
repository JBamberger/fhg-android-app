package de.jbamberger.fhgapp.ui.feed


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jbamberger.fhgapp.R
import de.jbamberger.fhgapp.ui.components.BaseFragment

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
class FeedFragment : BaseFragment<FeedViewModel>() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.refreshable_list_fragment, container, false)
    }

    override fun getViewModelClass(): Class<FeedViewModel> {
        return FeedViewModel::class.java
    }
}
