package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R

class NavigationDeciderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // This fragment doesn't have a UI
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val encryptedInfo = activity?.intent?.getByteArrayExtra("encryptedInfo")

        if (encryptedInfo?.isNotEmpty() == true) {
            findNavController().navigate(R.id.action_navigationDecider_to_homeFragment)
            activity?.intent?.removeExtra("encryptedInfo") // Clear the extra to prevent re-navigation
        } else {
            findNavController().navigate(R.id.action_navigationDecider_to_loginFragment)
        }
    }
}
