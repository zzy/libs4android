package biz.ouds.android.zoom;

import android.util.Log;
import android.widget.TextView;

public class ZoomTextView extends ZoomView<TextView> {

	private final String TAG = getClass().getName();

	public static final float MIN_TEXT_SIZE = 5.0f;
	public static final float MAX_TEXT_SIZE = 200.0f;

	public ZoomTextView(TextView view, float scale) {
		super(view, scale);
	}

	@Override
	protected void zoomOut() {
		Log.i(TAG, "缩小……");
		
		view.setTextSize(view.getTextSize() - zoomScale);
		if (view.getTextSize() < MIN_TEXT_SIZE)
			view.setTextSize(MIN_TEXT_SIZE);
	}

	@Override
	protected void zoomIn() {
		Log.i(TAG, "放大……");
		
		view.setTextSize(view.getTextSize() + zoomScale);
		if (view.getTextSize() > MAX_TEXT_SIZE)
			view.setTextSize(MAX_TEXT_SIZE);
	}

}
