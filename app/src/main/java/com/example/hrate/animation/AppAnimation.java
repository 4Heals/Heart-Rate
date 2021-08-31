package com.example.hrate.animation;

import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

public class AppAnimation {
    public static void changeGuidelineWithAnim(final Guideline guideline, float value){
        final ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)guideline.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(lp.guidePercent, value);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                guideline.setGuidelinePercent((float)valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }
}
