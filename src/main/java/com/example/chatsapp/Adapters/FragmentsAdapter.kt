package com.example.chatsapp.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chatsapp.Fragments.CallsFragment
import com.example.chatsapp.Fragments.ChatsFragment
import com.example.chatsapp.Fragments.StatusFragment

class FragmentsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return ChatsFragment()
            1 -> return StatusFragment()
            2 -> return CallsFragment()
            else -> return ChatsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? =  null
        if (position == 0){
            title = "CHATS"
        }
        if (position == 1){
            title = "STATUS"
        }
        if (position == 2){
            title = "CALLS"
        }
        return title
    }
}