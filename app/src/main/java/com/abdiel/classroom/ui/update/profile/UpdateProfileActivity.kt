package com.abdiel.classroom.ui.update.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.classroom.R
import com.abdiel.classroom.base.activity.BaseActivity
import com.abdiel.classroom.data.school.School
import com.abdiel.classroom.databinding.ActivityUpdateProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.helper.DateTimeHelper
import com.crocodic.core.helper.StringHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class UpdateProfileActivity :
    BaseActivity<ActivityUpdateProfileBinding, UpdateProfileViewModel>(R.layout.activity_update_profile) {

    private var schoolId: Int? = null
    private val schools = ArrayList<School?>()
    private var idUser: Int? = null
    private var photoFile: File? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        getUser()

        binding.etSchool.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val item = schools[p2]
                schoolId = item?.id ?: 0
            }
        }

        binding.btnEditProfile.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()
            }
        }

        binding.btnSave.setOnClickListener {
            validateForm()
        }

        viewModel.getSchool()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("On Progress")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }

                launch {
                    viewModel.schools.collect {
                        val arrayAdapter =
                            ArrayAdapter(this@UpdateProfileActivity, R.layout.dropdown_item, it)
                        binding.etSchool.setAdapter(arrayAdapter)
                        schools.clear()
                        schools.addAll(it)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (photoFile != null) {
            unsavedAlert()
            return
        }

        if (binding.etName.textOf().isNotEmpty() && binding.etName.textOf() != userName) {
            unsavedAlert()
            return
        }

        if (schoolId != null || binding.data?.schoolId != schoolId) {
            unsavedAlert()
            return
        }
        finish()
    }

    private fun validateForm() {
//        if (idUser == null) {
//            binding.root.snacked("Id user is empty")
//            return
//        }
        val name = binding.etName.textOf()

        if (photoFile == null) {
            if (name.isEmpty() || schoolId == null) {
                binding.root.snacked("Name or school is empty")
            } else {
                Log.d("foto", "photo null")
                viewModel.updateProfile(name, schoolId!!)
            }
        } else {
            if (name.isEmpty() || schoolId == null) {
                binding.root.snacked("Name or school is empty")
            } else {
                Log.d("foto", "photo not null")
                viewModel.updateProfileImg(name, schoolId!!, photoFile!!)
            }
        }
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        binding.data = users
        idUser = users?.id
        userName = users?.name
        schoolId = users?.schoolId

    }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }

            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            binding.cvProfile.setImageBitmap(bitmap)
            val requestOption = RequestOptions()
                .circleCrop()
            Glide
                .with(this)
                .load(bitmap)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOption)
                .into(binding.cvProfile)
            photoFile = file
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak dapat digunakan")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file
    }

    private fun unsavedAlert() {
        val alertDialog = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog_update, findViewById(R.id.alert_dialog_update))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(alertDialog)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val btnContinue = alertDialog.findViewById<AppCompatButton>(R.id.btn_continue)
        val btnDiscard = alertDialog.findViewById<AppCompatButton>(R.id.btn_discard)

        btnContinue.setOnClickListener {
            dialog.dismiss()
        }

        btnDiscard.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }
}