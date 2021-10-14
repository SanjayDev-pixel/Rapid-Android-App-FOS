package com.finance.app.utility

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class UploadData(fragment: Fragment, mContext: Context) {
    companion object {
        private const val SELECT_PDF_CODE = 1
        private const val CLICK_IMAGE_CODE = 2
        private const val SELECT_IMAGE_CODE = 3
    }

    private val selectionOption = arrayOf<CharSequence>("Take Photo",
            "Select Photo", "Select PDF", "Cancel")
    private val builders = AlertDialog.Builder(mContext)

    init {
        builders.setTitle("Upload Form")
        builders.setItems(selectionOption) { dialog, item ->
            when {
                selectionOption[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    fragment.startActivityForResult(intent, CLICK_IMAGE_CODE)
                }
                selectionOption[item] == "Select Photo" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    fragment.startActivityForResult(intent, SELECT_IMAGE_CODE)
                }
                selectionOption[item] == "Select PDF" -> {
                    val browseStorage = Intent(Intent.ACTION_GET_CONTENT)
                    browseStorage.type = "application/pdf"
                    fragment.startActivityForResult(browseStorage, SELECT_PDF_CODE)
                }
                selectionOption[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builders.show()
    }
}
