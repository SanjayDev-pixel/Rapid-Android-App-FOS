package com.finance.app.view.utils

import android.widget.AutoCompleteTextView
import com.finance.app.persistence.model.DropdownMaster
import fr.ganfra.materialspinner.MaterialSpinner
import java.util.*


fun MaterialSpinner.setSelectionFromList(dropDowns: ArrayList<DropdownMaster>, value: Int) {
    dropDowns.forEachIndexed { index, dropdownMaster ->
        if (dropdownMaster.typeDetailID == value) {
            this.setSelection(index + 1)
            return@forEachIndexed
        }
    }
}

fun ArrayList<DropdownMaster>.getDisplayText(typeDetailID: Int?): String {
    val list = this.filter { it.typeDetailID == typeDetailID }
    if (list.isNullOrEmpty().not()) return list[0].typeDetailDisplayText.toString()
    return ""
}
fun ArrayList<DropdownMaster>.getTypeDetailId(typeDetailText : String ?) : Int{
    val list = this.filter { it.typeDetailDisplayText.equals(typeDetailText,true) }
    if(list.isNullOrEmpty().not()) return list[0].typeDetailID
    return 0
}


fun AutoCompleteTextView.selectItem(text: String, position: Int = 0) {
    this.setText(text)
    this.showDropDown()
//    this.setSelection(position)
    this.listSelection = position
    this.performCompletion()
}
