package com.rahul.natureplant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahul.natureplant.model.Category

class CategoryViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val categoryList = listOf(
            Category("Succulent", "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"),

            Category("Indoor", "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"),

            Category("Decorative", "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"),

            Category("Hanging", "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"),

            Category("Office", "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"),

            Category("Outdoor", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"),

            Category("Succulent", "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"),

            Category("Indoor", "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"),

            Category("Decorative", "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"),

            Category("Hanging", "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"),

            Category("Office", "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"),

            Category("Outdoor", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80")

        )
        _categories.value = categoryList
    }
}
