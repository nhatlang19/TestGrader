package BubbleSheetScanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Binarize {
	public static Mat threshold(Mat warped) {
		Mat thresh = new Mat(warped.rows(), warped.cols(), CvType.CV_8UC3);
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		Utils.write("output/4_threshold.jpg", thresh);
		
		return thresh;
	}
}
