package motobeans.architecture.util

import android.view.View
import android.view.animation.TranslateAnimation

/**
 * Created by abul on 15/4/18.
 */

public class SlideViewUtils {

    /*********************
     * Hide visible layout
     * ********************/
// slide the view from below itself to the current position
    companion object {
        fun slideUp(view: View) {
            val animate = TranslateAnimation(
                0f, // fromXDelta
                0f, // toXDelta
                0f, // fromYDelta
                -view.getHeight().toFloat())                // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
        }

        // slide the view from its current position to below itself
        fun slideDown(view: View) {
            val animate = TranslateAnimation(
                0f, // fromXDelta
                0f, // toXDelta
                -view.getHeight().toFloat(), // fromYDelta
                0f) // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
        }

        fun slideLeft(view: View) {
            val animate = TranslateAnimation(
                view.getWidth().toFloat(), // fromXDelta
                0f, // toXDelta
                0f, // fromYDelta
                0f)                // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
        }

        fun slideRight(view: View) {
            val animate = TranslateAnimation(
                0f, // fromXDelta
                view.getWidth().toFloat(), // toXDelta
                0f, // fromYDelta
                0f) // toYDelta
            animate.duration = 500
            animate.fillAfter = true
            view.startAnimation(animate)
        }
    }

}