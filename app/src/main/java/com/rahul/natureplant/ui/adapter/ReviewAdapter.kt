package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rahul.natureplant.databinding.ItemSellerReviewBinding

data class Review(val name: String, val time: String, val text: String, val rating: Float)

class ReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemSellerReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(private val binding: ItemSellerReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvReviewerName.text = review.name
            binding.tvReviewTime.text = review.time
            binding.tvReviewText.text = review.text
            binding.reviewRatingBar.rating = review.rating
            binding.tvReviewRatingValue.text = review.rating.toString()
        }
    }
}
