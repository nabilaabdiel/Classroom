package com.abdiel.classroom.helper

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.abdiel.classroom.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.helper.StringHelper
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Response.error
import kotlin.math.round


class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl", "userName"], requireAll = true)
        fun loadImageRecipe(
            view: ImageView,
            imageUrl: String?,
            userName: String?
        ) {
            view.post {
                view.setImageDrawable(null)

                //setiap view beda".. klo km mau ada circle iv dan iv bikin 2 ok? ook

//            userName?.let {
//                val initialImage = StringHelper.generateTextDrawable(it, ContextCompat.getColor(view.context, R.color.black), 10)
//                Glide
//                    .with(view.context)
//                    .load(initialImage)
//                    .apply(RequestOptions.centerCropTransform())
//                    .error(R.drawable.ic_error)
//                    .into(view)
//            }

                //imageRound nya ntr di apus yak

                val avatar = StringHelper.generateTextDrawable(
                    StringHelper.getInitial(userName?.trim()),
                    ContextCompat.getColor(view.context, R.color.teal_700), view.measuredWidth
                )

                Log.d("BindingHelper", "Initial: ${StringHelper.getInitial(userName?.trim())}")
                Log.d("BindingHelper", "Image: $imageUrl")


                if (imageUrl.isNullOrEmpty()) {
                    view.setImageDrawable(avatar)
                } else {
                    val requestOption = RequestOptions()
                        .placeholder(avatar)
                        .circleCrop()
                    Glide
                        .with(view.context)
                        .load(StringHelper.validateEmpty(imageUrl))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(requestOption)
                        .error(avatar)
                        .into(view)
                }

            }
//            imageUrl?.let {
//                Glide
//                    .with(view.context)
//                    .load(imageUrl)
//                    .apply(RequestOptions.centerCropTransform())
//                    .error(R.drawable.ic_error)
//                    .into(view)
//            }

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