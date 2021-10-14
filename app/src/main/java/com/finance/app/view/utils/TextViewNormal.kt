package com.finance.app.view.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class TextViewNormal : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context)
    {
        init(null);
    }
    constructor(context: Context , attributeSet: AttributeSet) :super(context,attributeSet)
    {
        init(context);
    }
    /* constructor(context: Context , attributeSet: AttributeSet , defStyle:Int) : super(context,attributeSet,defStyle)
     {
         init(context);
     }*/
    private fun init(context: Context?)
    {
        try {
            if(context!=null)
            {
                val myTypeface = Typeface.createFromAsset(context.assets,"fonts/montserrat.otf");
                typeface = myTypeface;
            }
        }
        catch(e:Exception)
        {
            e.printStackTrace();
        }
    }

}