package com.bridesandgrooms.event.UI.Fragments

import com.bridesandgrooms.event.UI.Adapters.GalleryAdapter
import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.AdManager
import com.bridesandgrooms.event.Functions.AdManagerSingleton
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.MVP.ActivityGalleryPresenter
import com.bridesandgrooms.event.Model.DashboardImage.DashboardRepository
import com.bridesandgrooms.event.Model.DashboardImage.FirebaseDataSourceImpl
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.databinding.ActivityGalleryBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar

class FragmentGallery : Fragment(), ActivityGalleryPresenter.ActiveGalleryImages {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var activityGP: ActivityGalleryPresenter
    private lateinit var binding: ActivityGalleryBinding

    private lateinit var toolbar: MaterialToolbar

    private var mContext: Context? = null
    private var showAds = false
    private lateinit var adManager: AdManager
    private val TAG = "InterstitialAd"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        showAds = RemoteConfigSingleton.get_showads()
        if (showAds) {
            adManager = AdManagerSingleton.getAdManager()
            adManager.loadInterstitialAd(requireActivity())
        }

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.categories)

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_gallery, container, false)

//        binding.loadingscreen.root.visibility = View.VISIBLE
//        binding.activitygalleryrv.visibility = View.INVISIBLE

        val category = arguments?.getString("category") ?: ""

        try {
            activityGP = ActivityGalleryPresenter(this)

            val repository = DashboardRepository(FirebaseDataSourceImpl(mContext!!))
            activityGP.setRepository(repository)
            activityGP.fetchActivityGallery(category)

        } catch (e: Exception) {
            Log.e(DashboardEvent.TAG, e.message.toString())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (showAds) {
            MobileAds.initialize(requireContext()) { initializationStatus ->
                // You can leave this empty or handle initialization status if needed
            }
        }
    }

    override fun onActiveGalleryImages(images: List<Triple<Bitmap, String, String>>) {
        if (showAds) {
            adManager.showInterstitialAd(requireActivity())
        }

        binding.loadingscreen.root.visibility = View.INVISIBLE

        recyclerViewCategory = binding.activitygalleryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }
        val rvAdapter = GalleryAdapter(mContext!!, images)
        binding.activitygalleryrv.adapter = rvAdapter
    }

    override fun onActiveGalleryImagesError(errcode: String) {
        TODO("Not yet implemented")
    }
}