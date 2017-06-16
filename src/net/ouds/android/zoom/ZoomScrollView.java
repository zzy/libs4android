package biz.ouds.android.zoom;

import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;

public class ZoomScrollView extends ZoomView<ScrollView> {

	private final String TAG = getClass().getName();

	public static final int MIN_VIEW_WIDTH = 200;
	public static final int MIN_VIEW_HEIGHT = 230;
	
	public static final int MAX_VIEW_WIDTH = 800;
	public static final int MAX_VIEW_HEIGHT = 950;
	
	private int width;
	private int height;
	
	private int resetWidth;
	private int resetHeight;

	public ZoomScrollView(ScrollView view, float scale) {
		super(view, scale);
	}

	@Override
	protected void zoomOut() {
		Log.i(TAG, "缩小……");
		
		width = view.getWidth();
		resetWidth = (int) (width - zoomScale);
		if (resetWidth < MIN_VIEW_WIDTH)
			resetWidth = MIN_VIEW_WIDTH;
		
		height = view.getHeight();
		resetHeight = (int) (height - zoomScale);
		if (resetHeight < MIN_VIEW_HEIGHT)
			resetHeight = MIN_VIEW_HEIGHT;
		
		view.setLayoutParams(new LayoutParams(resetWidth, resetHeight));
	}

	@Override
	protected void zoomIn() {
		Log.i(TAG, "放大……");
		
		width = view.getWidth();
		resetWidth = (int) (width + zoomScale);
		if (resetWidth > MAX_VIEW_WIDTH)
			resetWidth = MAX_VIEW_WIDTH;
		
		height = view.getHeight();
		resetHeight = (int) (height + zoomScale);
		if (resetHeight > MAX_VIEW_HEIGHT)
			resetHeight = MAX_VIEW_HEIGHT;
		
		view.setLayoutParams(new LayoutParams(resetWidth, resetHeight));
	}

}
