package com.example.proto04.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proto04.databinding.FragmentCommunityBinding

class CommunityFragment : Fragment() {

    private var fBinding : FragmentCommunityBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentCommunityBinding.inflate(inflater, container, false)

        fBinding = binding

        return fBinding?.root
    }

    override fun onDestroyView() {
        fBinding = null
        super.onDestroyView()
    }
}