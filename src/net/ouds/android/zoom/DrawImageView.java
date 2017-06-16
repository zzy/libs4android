package biz.ouds.android.zoom;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

public class DrawImageView {

	private final String TAG = getClass().getName();
	
	/**
	 * 绘制原生大小的 ImageView
	 * @param view
	 * @param is
	 * @param scale
	 */
	public DrawImageView(ImageView view, InputStream is, int scale, boolean filter) {
		Log.i(TAG, "缩放图片……");
		
		Rect padding = new Rect(0, 0, 0, 0);
		Options options = new Options();
		options.inJustDecodeBounds = true;

		Bitmap bm = BitmapFactory.decodeStream(is, padding, options); // 此时返回bm为空

		options.inJustDecodeBounds = false; // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false

//		options.outWidth = width;
//		options.outHeight = height;
				
//		int scale = (int)(options.outHeight / (float) height); // 缩放比
//		if (scale <= 0)
//			scale = 1;
		
		options.inSampleSize = scale;

		bm = BitmapFactory.decodeStream(is, padding, options);
		
		view.setImageBitmap(Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), filter));
	}
	
	/**
	 * 绘制指定大小的 ImageView
	 * @param view
	 * @param is
	 * @param scale
	 */
	public DrawImageView(ImageView view, InputStream is, int scale, int initWidth, int initHeight, boolean filter) {
		Log.i(TAG, "缩放图片……");
		
		Rect padding = new Rect(0, 0, 0, 0);
		Options options = new Options();
		options.inJustDecodeBounds = true;

		Bitmap bm = BitmapFactory.decodeStream(is, padding, options); // 此时返回bm为空

		options.inJustDecodeBounds = false; // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false

//		options.outWidth = width;
//		options.outHeight = height;
				
//		int scale = (int)(options.outHeight / (float) height); // 缩放比
//		if (scale <= 0)
//			scale = 1;
		
		options.inSampleSize = scale;

		bm = BitmapFactory.decodeStream(is, padding, options);
		
		view.setImageBitmap(Bitmap.createScaledBitmap(bm, initWidth, initHeight, filter));
	}

}
