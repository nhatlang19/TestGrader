import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;

public class Hello {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String nameFile = "test3";
		String sourceFileName = "images/" + nameFile + ".png";
		// read images
		Mat sourceMat = Imgcodecs.imread(sourceFileName);
		System.out.println("width, height = " + sourceMat.width() + ", " + sourceMat.height());

		Mat gray = new Mat();
		Imgproc.cvtColor(sourceMat, gray, Imgproc.COLOR_BGR2GRAY);
		write("output/" + nameFile + "_step0.jpg", gray);

		Mat blurred = new Mat();
		Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

		Mat edged = new Mat();
		Imgproc.Canny(blurred, edged, 75, 200);
		// Imgproc.threshold(sourceMat, edges, 0, 255, Imgproc.THRESH_BINARY);
		write("output/" + nameFile + "_step1.jpg", edged);

		// tim tat ca cac canh
		List<MatOfPoint> contourList = new ArrayList<>();
		Imgproc.findContours(edged, contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat copy = new Mat();
		copy.create(edged.rows(), edged.cols(), CvType.CV_8UC3);
		for (int contourIdx = 0; contourIdx < contourList.size(); contourIdx++) {
			Imgproc.drawContours(edged, contourList, contourIdx, new Scalar(255), 3);
		}
		write("output/" + nameFile + "_step2.jpg", edged);

		// Sort
		contourList.sort((o1, o2) -> {
			double area1 = Imgproc.contourArea(o1);
			double area2 = Imgproc.contourArea(o2);

			if (area1 > area2)
				return -1;
			else if (area1 < area2)
				return 1;
			else
				return 0;
		});

		Rect rectMax = new Rect();
		int contourIdxMax = 0;
		MatOfPoint2f approxCurveMax = new MatOfPoint2f();
		for (int contourIdx = 0; contourIdx < contourList.size(); contourIdx++) {
			final MatOfPoint contour = contourList.get(contourIdx);
			// final Rect bb = Imgproc.boundingRect(contour);

			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
			if (approxCurve.toArray().length == 4) {
				// Convert back to MatOfPoint
				MatOfPoint points = new MatOfPoint(approxCurve.toArray());
				// Get bounding rect of contour
				Rect rect = Imgproc.boundingRect(points);
				System.out.println(rect.area());
				approxCurveMax = approxCurve;
				contourIdxMax = contourIdx;
				break;
			}
		}
		Mat copy2 = new Mat();
		gray.copyTo(copy2);
		// copy2.create(edged.rows(), edged.cols(), CvType.CV_8UC3);
		Imgproc.drawContours(copy2, contourList, contourIdxMax, new Scalar(111), 3);
		// Imgproc.rectangle(gray, rectMax.tl(), rectMax.br(), new Scalar(255), 2);
		write("output/" + nameFile + "_step3.jpg", copy2);

		////////////////////////////////////////////////////////////////
		///////////////////////// STEP 2 /////////////////////////////
		// calculate the center of mass of our contour image using moments
		Moments moment = Imgproc.moments(approxCurveMax.col(0));
		int x = (int) (moment.get_m10() / moment.get_m00());
		int y = (int) (moment.get_m01() / moment.get_m00());

		// SORT POINTS RELATIVE TO CENTER OF MASS
		Point[] sortedPoints = new Point[4];

		double[] data;
		int count = 0;
		for (int i = 0; i < approxCurveMax.col(0).rows(); i++) {
			data = approxCurveMax.col(0).get(i, 0);
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
		/*
		 * # compute the width of the new image, which will be the # maximum distance
		 * between bottom-right and bottom-left # x-coordiates or the top-right and
		 * top-left x-coordinates widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] -
		 * bl[1]) ** 2)) widthB = np.sqrt(((tr[0] - tl[0]) ** 2) + ((tr[1] - tl[1]) **
		 * 2)) maxWidth = max(int(widthA), int(widthB))
		 * 
		 * # compute the height of the new image, which will be the # maximum distance
		 * between the top-right and bottom-right # y-coordinates or the top-left and
		 * bottom-left y-coordinates heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1]
		 * - br[1]) ** 2)) heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1])
		 * ** 2)) maxHeight = max(int(heightA), int(heightB))
		 */
		System.out.println(sortedPoints[0]);
		System.out.println(sortedPoints[1]);
		System.out.println(sortedPoints[2]);
		System.out.println(sortedPoints[3]);
		Point bl = sortedPoints[0];
		Point br = sortedPoints[1];
		Point tl = sortedPoints[2];
		Point tr = sortedPoints[3];

		double widthA = Math.sqrt(Math.pow(br.x - bl.x, 2) + Math.pow(br.y - bl.y, 2));
		double widthB = Math.sqrt(Math.pow(tr.x - tl.x, 2) + Math.pow(tr.y - tl.y, 2));
		int maxWidth = (int) Math.max(widthA, widthB);
		System.out.println(maxWidth);

		double heightA = Math.sqrt(Math.pow(tr.x - br.x, 2) + Math.pow(tr.y - br.y, 2));
		double heightB = Math.sqrt(Math.pow(tl.x - bl.x, 2) + Math.pow(tl.y - bl.y, 2));
		int maxHeight = (int) Math.max(heightA, heightB);
		System.out.println(maxHeight);

		MatOfPoint2f src = new MatOfPoint2f(sortedPoints[0], sortedPoints[1], sortedPoints[2], sortedPoints[3]);

		MatOfPoint2f dst = new MatOfPoint2f(new Point(0, 0), new Point(maxWidth - 1, 0), new Point(0, maxHeight - 1),
				new Point(maxWidth - 1, maxHeight - 1));

		Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
		// This is you new image as Mat
		Mat warped = new Mat();
		Imgproc.warpPerspective(gray, warped, warpMat, new Size(maxWidth, maxHeight));
		// save image
		write("output/" + nameFile + "_step4.jpg", warped);

		////////////////////////////////////////////////////////////////
		///////////////////////// STEP 3 /////////////////////////////
		Mat thresh = new Mat();
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		write("output/" + nameFile + "_binarize.jpg", thresh);
	}

	private static void write(String output, Mat source) {
		System.out.println("Writefile: " + output);
		Imgcodecs.imwrite(output, source);
	}
}
