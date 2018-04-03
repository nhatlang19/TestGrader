package BubbleSheetScanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class FilterCircle {
	public static List<MatOfPoint> filterCircleAnswers(Mat thresh) {
		List<MatOfPoint> questionCnts = new ArrayList<>();
		List<MatOfPoint> cnts = new ArrayList<>();
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat copy3 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
		for (int contourIdx = 0; contourIdx < cnts.size(); contourIdx++) {
			final MatOfPoint contour = cnts.get(contourIdx);
			final Rect bb = Imgproc.boundingRect(contour);
			float ar = bb.width / (float) bb.height;

			System.out.println("width:" + bb.width + ",height: " + bb.height + ",ar: " + ar);
			if (bb.width >= 34 && bb.height >= 34 && ar >= 0.9 && ar <= 1.1) {
				questionCnts.add(contour);
				Imgproc.drawContours(copy3, cnts, contourIdx, new Scalar(0, 0, 255), 1);
			}
		}
		Utils.write("output/5_find_circle.jpg", copy3);
		copy3.release();
		System.out.println("Count Questions: " + questionCnts.size());
		return questionCnts;
	}
	
	public static List<MatOfPoint> filterCircleTopic(Mat thresh) {
		List<MatOfPoint> questionCnts = new ArrayList<>();
		List<MatOfPoint> cnts = new ArrayList<>();
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat copy3 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
		for (int contourIdx = 0; contourIdx < cnts.size(); contourIdx++) {
			final MatOfPoint contour = cnts.get(contourIdx);
			final Rect bb = Imgproc.boundingRect(contour);
			float ar = bb.width / (float)bb.height;
			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
			System.out.println("-- (" + bb.width + "," + bb.height + "), ar: " + ar);
			if (approxCurve.toArray().length != 4 && bb.width >= 18 && bb.height >= 18 && ar >= 0.9 && ar <= 1.2) {
				System.out.println("(" + bb.width + "," + bb.height + "), ar: " + ar);
				questionCnts.add(contour);
				Imgproc.drawContours(copy3, cnts, contourIdx, new Scalar(0,0,255), 1);
			}
		}
		
		Utils.write("output/5_find_circle.jpg", copy3);
		copy3.release();
		System.out.println("Count topic circle: " + questionCnts.size());
		return questionCnts;
	}
	
	public static List<MatOfPoint> sortContours(Mat thresh, List<MatOfPoint> questionCnts) {
		Mat copy4 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
		int count = 0;
		for (int q = 0; q < questionCnts.size(); q++) {
			final MatOfPoint contour = questionCnts.get(q);
			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			Moments moment = Imgproc.moments(contour2f.col(0));
			int cX = (int) (moment.get_m10() / moment.get_m00());
			int cY = (int) (moment.get_m01() / moment.get_m00());

			final Rect bb = Imgproc.boundingRect(questionCnts.get(q));
			Imgproc.drawContours(copy4, questionCnts, q, new Scalar(0, 0, 255), 1);
			Imgproc.putText(copy4, ++count + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
					new Scalar(255, 255, 255), 2);
		}
		Utils.write("output/6_number.jpg", copy4);

		Collections.reverse(questionCnts); // @TODO: need to improve in here

		Mat copy5 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
		count = 0;
		for (int q = 0; q < questionCnts.size(); q++) {
			final MatOfPoint contour = questionCnts.get(q);
			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			Moments moment = Imgproc.moments(contour2f.col(0));
			int cX = (int) (moment.get_m10() / moment.get_m00());
			int cY = (int) (moment.get_m01() / moment.get_m00());

			final Rect bb = Imgproc.boundingRect(questionCnts.get(q));
			Imgproc.drawContours(copy5, questionCnts, q, new Scalar(0, 0, 255), 1);
			Imgproc.putText(copy5, ++count + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
					new Scalar(255, 255, 255), 2);
		}
		Utils.write("output/7_number_after_sort.jpg", copy5);
		
		return questionCnts;
	}
}
