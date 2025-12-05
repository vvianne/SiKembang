package com.example.sikembang.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sikembang.utils.LocationHelper

class PosyanduLokasiViewModelFactory(
    private val locationHelper: LocationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosyanduLokasiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosyanduLokasiViewModel(locationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class, coba jangan jahil ya 😎")
    }
}
