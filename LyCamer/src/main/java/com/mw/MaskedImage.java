package com.mw;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public abstract class MaskedImage extends ImageView {
	private Xfermode MASK_XFERMODE;
	private Bitmap mask;
	private Paint paint;
	Drawable localDrawable;
	Paint localPaint1;
	private void ctor() {
		PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
		MASK_XFERMODE = new PorterDuffXfermode(localMode);
		localDrawable=null;
		localPaint1 = new Paint();
		this.paint = localPaint1;
		this.paint.setFilterBitmap(false);
		Paint localPaint2 = this.paint;
		Xfermode localXfermode1 = MASK_XFERMODE;
		@SuppressWarnings("unused")
		Xfermode localXfermode2 = localPaint2.setXfermode(localXfermode1);
		
	}

	public MaskedImage(Context paramContext) {
		super(paramContext);
		ctor();
	}

	public MaskedImage(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		ctor();
	}

	public MaskedImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		ctor();
	}

	
	public abstract Bitmap createMask();

	protected void onDraw(Canvas paramCanvas) {
		if (localDrawable == null)
			localDrawable = getDrawable();
		if (localDrawable == null)
			return;
		try {			
			float f1 = getWidth();
			float f2 = getHeight();
			int i = paramCanvas.saveLayer(0.0F, 0.0F, f1, f2, null, 31);
			int j = getWidth();
			int k = getHeight();
			localDrawable.setBounds(0, 0, j, k);
			localDrawable.draw(paramCanvas);
			if ((this.mask == null) || (this.mask.isRecycled())) {
				Bitmap localBitmap1 = createMask();
				this.mask = localBitmap1;
			}
			//Bitmap localBitmap2 = this.mask;
			//Paint localPaint3 = this.paint;
			paramCanvas.drawBitmap(this.mask, 0.0F, 0.0F, this.paint);
			//paramCanvas.restoreToCount(i);
			return;
		} catch (Exception localException) {
			//StringBuilder localStringBuilder = new StringBuilder()
					//.append("Attempting to draw with recycled bitmap. View ID = ");
			//System.out.println("localStringBuilder=="+localStringBuilder);
		}
	}
}