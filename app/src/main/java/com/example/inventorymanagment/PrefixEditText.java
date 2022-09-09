package com.example.inventorymanagment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class PrefixEditText extends AppCompatEditText {
    float mOriginalLeftPadding = -1.0f;

    public PrefixEditText(Context context) {
        super(context);
    }

    public PrefixEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PrefixEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        calculatePrefix();
    }

    private void calculatePrefix() {
        if (this.mOriginalLeftPadding == -1.0f) {
            String str = (String) getTag();
            float[] fArr = new float[str.length()];
            getPaint().getTextWidths(str, fArr);
            float f = 0.0f;
            for (float f2 : fArr) {
                f += f2;
            }
            this.mOriginalLeftPadding = (float) getCompoundPaddingLeft();
            setPadding((int) (f + this.mOriginalLeftPadding), getPaddingRight(), getPaddingTop(), getPaddingBottom());
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText((String) getTag(), this.mOriginalLeftPadding, (float) getLineBounds(0, (Rect) null), getPaint());
    }
}
