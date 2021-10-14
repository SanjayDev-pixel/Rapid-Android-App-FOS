package com.finance.app.view.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore


//Will Only Pick Image and PDF file...
fun Activity.startFilePickerActivity(requestCode: Int) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    val mimeTypes = arrayOf("image/*", "application/pdf")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    startActivityForResult(intent, requestCode)
}

//Will Only Pick Image...
fun Activity.startImagePickerActivity(requestCode: Int, imageUri: Uri) {

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    startActivityForResult(intent, requestCode)
}

//Return Uri for image to pick using startImagePickerActivity() method...
fun Activity.getImageUriForImagePicker(imageTitle: String): Uri? {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, imageTitle)
    values.put(MediaStore.Images.Media.DESCRIPTION, "")
    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
}
