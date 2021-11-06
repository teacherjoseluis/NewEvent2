//package com.example.newevent2

// class NewTask_PagerAdapter(
//    private val myContext: Context,
//    fm: FragmentManager,
//    internal var totalTabs: Int,
//    val eventkey: String
//
//) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

//    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> {
//                val bundle = Bundle()
//                bundle.putString("eventkey", eventkey)
//                val fragInfo = NewTask_TaskDetail()
//                fragInfo.arguments = bundle
//                return fragInfo
//            }
//            1 -> {
//                val bundle = Bundle()
//                bundle.putString("eventkey", eventkey)
//                val fragInfo = NewTask_PaymentDetail()
//                fragInfo.arguments = bundle
//                return fragInfo
//            }
//            else -> {
//                val bundle = Bundle()
//                bundle.putString("eventkey", eventkey)
//                val fragInfo = NewTask_TaskDetail()
//                fragInfo.arguments = bundle
//                return fragInfo
//            }
//        }
//    }

//    override fun getCount(): Int {
//        return totalTabs
//    }
//
//}

