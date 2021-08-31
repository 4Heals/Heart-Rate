package com.example.hrate.theme;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import uk.co.samuelwall.materialtaptargetprompt.extras.PromptOptions;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.CirclePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;

public class MTTPCustom {
    public static class DimmedCirclePromptBackground extends CirclePromptBackground
    {
        @NonNull
        private RectF dimBounds = new RectF();
        @NonNull private Paint dimPaint;

        public DimmedCirclePromptBackground()
        {
            dimPaint = new Paint();
            dimPaint.setColor(Color.BLACK);
        }

        @Override
        public void prepare(@NonNull final PromptOptions options, final boolean clipToBounds, @NonNull Rect clipBounds)
        {
            super.prepare(options, clipToBounds, clipBounds);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            // Set the bounds to display as dimmed to the screen bounds
            dimBounds.set(0, 0, metrics.widthPixels, metrics.heightPixels);
        }

        @Override
        public void update(@NonNull final PromptOptions options, float revealModifier, float alphaModifier)
        {
            super.update(options, revealModifier, alphaModifier);
            // Allow for the dimmed background to fade in and out
            this.dimPaint.setAlpha((int) (200 * alphaModifier));
        }

        @Override
        public void draw(@NonNull Canvas canvas)
        {
            // Draw the dimmed background
            canvas.drawRect(this.dimBounds, this.dimPaint);
            // Draw the background
            super.draw(canvas);
        }
    }

    public static class DimmedRectPromptBackground extends RectanglePromptBackground
    {
        @NonNull
        private RectF dimBounds = new RectF();
        @NonNull private Paint dimPaint;

        public DimmedRectPromptBackground()
        {
            dimPaint = new Paint();
            dimPaint.setColor(Color.BLACK);
        }

        @Override
        public void prepare(@NonNull final PromptOptions options, final boolean clipToBounds, @NonNull Rect clipBounds)
        {
            super.prepare(options, clipToBounds, clipBounds);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            // Set the bounds to display as dimmed to the screen bounds
            dimBounds.set(0, 0, metrics.widthPixels, metrics.heightPixels);
        }

        @Override
        public void update(@NonNull final PromptOptions options, float revealModifier, float alphaModifier)
        {
            super.update(options, revealModifier, alphaModifier);
            // Allow for the dimmed background to fade in and out
            this.dimPaint.setAlpha((int) (200 * alphaModifier));
        }

        @Override
        public void draw(@NonNull Canvas canvas)
        {
            // Draw the dimmed background
            canvas.drawRect(this.dimBounds, this.dimPaint);
            setCornerRadius(60f, 60f);
            // Draw the background
            super.draw(canvas);
        }
    }
}
