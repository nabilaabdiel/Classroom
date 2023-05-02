package com.abdiel.classroom.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.abdiel.classroom.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlin.math.round


class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl", "imageRound"], requireAll = false)
        fun loadImageRecipe(view: ImageView, imageUrl: String?, imageRound: Int?) {

            view.setImageDrawable(null)

            imageUrl?.let {
                Glide
                    .with(view.context)
                    .load(imageUrl)
//                    .placeholder(R.drawable.ic_person)
                    .apply(RequestOptions.centerCropTransform())
//                    .error(R.drawable.error)
                    .into(view)

            }

//            imageRound?.let {
//                Glide
//                    .with(view.context)
//                    .load(imageUrl)
//                    .transform(RoundedCorners(round))
//                    .placeholder(R.drawable.ic_person)
//                    .apply(RequestOptions.circleCropTransform())
//                    .error(R.drawable.error)
//                    .into(view)
//
//            }
        }

        @JvmStatic
        @BindingAdapter(value = ["profilePhoto"], requireAll = false)
        fun profilePhoto(view: ImageView, profilePhoto: String?) {

            view.setImageDrawable(null)

            profilePhoto?.let {
                Glide
                    .with(view.context)
                    .load(profilePhoto)
//                    .placeholder(R.drawable.placeholder)
                    .apply(RequestOptions.centerCropTransform())
//                    .error(R.drawable.error)
                    .into(view)

            }

//            imageRound?.let {
//                Glide
//                    .with(view.context)
//                    .load(imageUrl)
//                    .transform(RoundedCorners(round))
//                    .placeholder(R.drawable.ic_person)
//                    .apply(RequestOptions.circleCropTransform())
//                    .error(R.drawable.error)
//                    .into(view)
//
//            }
        }
    }
}