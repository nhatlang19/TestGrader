package BubbleSheetScanner;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class PerspectiveTransform {
	private static Point[] orderPoints(MatOfPoint2f approxCurve) {
		// calculate the center of mass of our contour image using moments
		Moments moment = Imgproc.moments(approxCurve.col(0));
		int x = (int) (moment.get_m10() / moment.get_m00());
		int y = (int) (moment.get_m01() / moment.get_m00());

		// SORT POINTS RELATIVE TO CENTER OF MASS
		Point[] sortedPoints = new Point[4];

		double[] data;
		int count = 0;
		for (int i = 0; i < approxCurve.col(0).rows(); i++) {
			data = approxCurve.col(0).get(i, 0);
			double datax = data[0];
			double datay = data[1];
			if (datax < x && datay < y) {
				sortedPoints[0] = new Point(datax, datay);
				count++;
			} else if (datax > x && datay < y) {
				sortedPoints[1] = new Point(datax, datay);
				count++;
			} else if (datax < x && datay > y) {
				sortedPoints[2] = new Point(datax, datay);
				count++;
			} else if (datax > x && datay > y) {
				sortedPoints[3] = new Point(datax, datay);
				count++;
			}
		}
		
		return sortedPoints;
	}
	
	public static Mat transform(Mat gray, MatOfPoint2f approxCurve) {
		Point[] sortedPoints = orderPoints(approxCurve);
		
		Point bl = sortedPoints[0];
		Point br = sortedPoints[1];
		Point tl = sortedPoints[2];
		Point tr = sortedPoints[3];
		
		double widthA = Math.sqrt(Math.pow(br.x - bl.x, 2) + Math.pow(br.y - bl.y, 2));
		double widthB = Math.sqrt(Math.pow(tr.x - tl.x, 2) + Math.pow(tr.y - tl.y, 2));
		int maxWidth = (int) Math.max(widthA, widthB);

		double heightA = Math.sqrt(Math.pow(tr.x - br.x, 2) + Math.pow(tr.y - br.y, 2));
		double heightB = Math.sqrt(Math.pow(tl.x - bl.x, 2) + Math.pow(tl.y - bl.y, 2));
		int maxHeight = (int) Math.max(heightA, heightB);

		MatOfPoint2f src = new MatOfPoint2f(sortedPoints[0], sortedPoints[1], sortedPoints[2], sortedPoints[3]);

		MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(maxWidth - 1, 0), new Point(0, maxHeight - 1),
				new Point(maxWidth - 1, maxHeight - 1));
		
		Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
		
		Mat warped = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
		Imgproc.warpPerspective(gray, warped, warpMat, new Size(maxWidth, maxHeight));
		Utils.write("output/3_perspective_transform.jpg", warped);
		
		return warped;
	}
}
