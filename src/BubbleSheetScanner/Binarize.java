package BubbleSheetScanner;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Binarize {
	private Mat warped;
	private Mat thresh;
	
	public Mat getThresh() {
		return thresh;
	}

	public Binarize(Mat warped) {
		this.thresh = new Mat();
		this.warped = warped;
	}
	
	public void threshold() {
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		Utils.write("output/3_threshold.jpg", thresh);
	}
}
