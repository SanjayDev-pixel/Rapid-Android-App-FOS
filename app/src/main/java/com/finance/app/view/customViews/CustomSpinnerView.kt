package com.finance.app.view.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.finance.app.R
import com.finance.app.others.AppEnums
import com.finance.app.presenter.connector.ValidationHandler
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.arrayadapter.CustomSpinnerAdapter
import com.finance.app.view.customViews.interfaces.IspinnerCustomView
import com.finance.app.view.customViews.interfaces.IspinnerMainView
import com.finance.app.view.customViews.interfaces.IspinnerModel
import fr.ganfra.materialspinner.MaterialSpinner
import motobeans.architecture.util.exGone
import motobeans.architecture.util.exVisible

@SuppressLint("ViewConstructor")
class CustomSpinnerView<Type : IspinnerModel>(val mContext: Context, private val dropDowns: ArrayList<Type>?,
                                              label: String, attrs: AttributeSet? = null,
                                              var isMandatory: Boolean = false,
                                              val iSpinnerMainView: IspinnerMainView<Type>? = null)
    : LinearLayout(mContext, attrs), AdapterView.OnItemSelectedListener,
        ValidationHandler, IspinnerCustomView<Type> {
    private lateinit var spinnerType: MaterialSpinner
    private lateinit var tvErrorText: TextView
    private lateinit var llErrorBlock: LinearLayout
    private lateinit var adapterExpendedType: CustomSpinnerAdapter<Type>

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.custom_spinner, this, true)
        initializeViews(rootView)
        proceedFurther()
        setDropdownLabel(label)
    }

    override fun initializeViews(rootView: View) {
        spinnerType = rootView.findViewById(R.id.spinnerType)
        tvErrorText = rootView.findViewById(R.id.tvErrorText)
        llErrorBlock = rootView.findViewById(R.id.llErrorBlock)
        spinnerType.onItemSelectedListener = this

        //DisablePersonalForm(binding)
            //dobProof.disableSelf()
    }

    private fun proceedFurther() {
        dropDowns?.let {
            adapterExpendedType = CustomSpinnerAdapter(mContext, R.layout.item_custom_spinner, dropDowns)
            spinnerType.adapter = adapterExpendedType
        }
    }

    private fun setDropdownLabel(msg: String) {
        val myTypeface = Typeface.createFromAsset(context.assets, "fonts/Raleway-Bold.ttf")
        spinnerType.hint = msg
        spinnerType.typeface = myTypeface
        spinnerType.floatingLabelText = msg

    }

    override fun resetValidation() {
        showError(false)
        spinnerType.setSelection(0)
    }

    override fun isValid(): Boolean {
        return when (isMandatory) {
            true -> validate()
            false -> !isMandatory
        }
    }

    override fun showError(isShow: Boolean) {
        when (isShow) {
            true -> llErrorBlock.exVisible()
            false -> llErrorBlock.exGone()
        }
    }

    private fun validate(): Boolean {
        val type = getSelectedValue()
        if (isMandatory && type == null) {
            spinnerType.error = mContext.getString(R.string.mandatory_field)
            return false
        }
        return true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position >= 0) {
            val value = parent?.getItemAtPosition(position) as Type
            iSpinnerMainView?.getSelectedValue(value)
        }
    }

    fun disableSelf() {
        val myTypeface = Typeface.createFromAsset(context.assets, "fonts/Raleway-Bold.ttf")
        spinnerType.disabledColor = R.color.black
        spinnerType.typeface = myTypeface
        spinnerType.isEnabled = false
    }

    fun enableSelf() {
        spinnerType.isEnabled = true
    }

    fun clearSelf() {
        spinnerType.setSelection(0)
    }

    override fun setSelection(iSpinnerValue: String?) {
        iSpinnerValue?.let {
            try {
                dropDowns?.let {
                    for (dd in dropDowns) {
                        if (dd.getCompareValue() == iSpinnerValue) {
                            spinnerType.setSelection((spinnerType.adapter as CustomSpinnerAdapter<Type?>)
                                    .getPosition(dd) + 1)
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getSelectedValue(): Type? {
        return try {
            spinnerType.selectedView.tag as Type
        } catch (e: Exception) {
            null
        }
    }
}