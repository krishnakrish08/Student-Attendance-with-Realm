package com.krishna.studentattendance.customradiobutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private float cornerRadius;
    private int strokeColor = Color.GRAY, strokeSize;
    private boolean hasStroke = false;

    private void init(Context context) {
        cornerRadius = ConversionHelper.dpToPx(context, 1);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    //Fixes incorrect outline drawn by default
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class ButtonOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), cornerRadius);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.clipPath(path);

        super.dispatchDraw(canvas);

        canvas.restoreToCount(count);

        if (hasStroke) {

            Rect rect = canvas.getClipBounds();

            RectF rectF = new RectF(strokeSize, strokeSize, rect.right - strokeSize, rect.bottom - strokeSize);

            Path clipPath = new Path();
            float corner = cornerRadius - strokeSize;
            clipPath.addRoundRect(rectF, corner, corner, Path.Direction.CW);
            canvas.clipPath(clipPath, Region.Op.DIFFERENCE);

            Paint p_stroke = new Paint();
            p_stroke.setAntiAlias(true);
            p_stroke.setColor(strokeColor);
            p_stroke.setStyle(Paint.Style.FILL);

            canvas.drawRoundRect(new RectF(rect), cornerRadius, cornerRadius, p_stroke);
        }
    }

    void setCornerRadius(float radius) {
        cornerRadius = radius;
        requestLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ButtonOutlineProvider());
        }
    }

    public void setStroke(boolean hasStroke) {
        this.hasStroke = hasStroke;
        invalidate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setStrokeSize(int strokeSize) {
        this.strokeSize = strokeSize;
        invalidate();
    }

}