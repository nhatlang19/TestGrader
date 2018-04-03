import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

public class TestGrader {

	private static void cleanOutput() {
		File directory = new File("output/");

		// Get all files in directory

		File[] files = directory.listFiles();
		for (File file : files) {
			// Delete each file

			if (!file.delete()) {
				// Failed to delete file
				System.out.println("Failed to delete " + file);
			}
		}
	}

	public static void main(String[] args) {
		cleanOutput();

		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String nameFile = "test3";
		String sourceFileName = "images/" + nameFile + ".png";
		// read images
		Mat sourceMat = Imgcodecs.imread(sourceFileName, Imgcodecs.IMREAD_ANYCOLOR);
		System.out.println("width, height = " + sourceMat.width() + ", " + sourceMat.height());

		Mat gray = new Mat(sourceMat.rows(), sourceMat.cols(), CvType.CV_8UC3);
		Imgproc.cvtColor(sourceMat, gray, Imgproc.COLOR_BGR2GRAY);
		write("output/" + nameFile + "_0_gray.jpg", gray);

		Mat blurred = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
		Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

		Mat edged = new Mat(blurred.rows(), blurred.cols(), CvType.CV_8UC3);
		Imgproc.Canny(blurred, edged, 75, 200);
		write("output/" + nameFile + "_1_edged.jpg", edged);

		// tim tat ca cac canh
		List<MatOfPoint> contourList = new ArrayList<>();
		Imgproc.findContours(edged, contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat copy = new Mat(edged.rows(), edged.cols(), CvType.CV_8UC3);
		for (int contourIdx = 0; contourIdx < contourList.size(); contourIdx++) {
			Imgproc.drawContours(copy, contourList, contourIdx, new Scalar(0, 0, 255), 3);
		}
		write("output/" + nameFile + "_2_find_contours.jpg", copy);

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
				approxCurveMax = approxCurve;
				contourIdxMax = contourIdx;
				break;
			}
		}
		Mat copy2 = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
		Imgproc.drawContours(copy2, contourList, contourIdxMax, new Scalar(0, 0, 255), 1);
		write("output/" + nameFile + "_3_big_rectangle.jpg", copy2);

		////////////////////////////////////////////////////////////////
		///////////////////////// STEP 2 /////////////////////////////
		Mat warped = PerspectiveTransform.transform(gray, approxCurveMax);
		write("output/" + nameFile + "_4_transform.jpg", warped);

		Mat paper = PerspectiveTransform.transform(sourceMat, approxCurveMax);
		write("output/" + nameFile + "_4_transform_paper.jpg", paper);

		////////////////////////////////////////////////////////////////
		///////////////////////// STEP 3 /////////////////////////////
		Mat thresh = new Mat(warped.rows(), warped.cols(), CvType.CV_8UC3);
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		write("output/" + nameFile + "_5_binarize.jpg", thresh);

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
		write("output/" + nameFile + "_6_find_circle.jpg", copy3);
		System.out.println(questionCnts.size());

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
		write("output/" + nameFile + "_7_number.jpg", copy4);

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
		write("output/" + nameFile + "_8_number_after_sort.jpg", copy5);

		int index = 0;

		List<Question> questions = new ArrayList<>();

		for (int q = 0; q < questionCnts.size(); q += 5) {
			Scalar scalar = null;
			switch (index) {
			case 0:
				scalar = new Scalar(0, 0, 255);
				break;
			case 1:
				scalar = new Scalar(255, 0, 0);
				break;
			case 2:
				scalar = new Scalar(0, 255, 0);
				break;
			case 3:
				scalar = new Scalar(255, 0, 255);
				break;
			case 4:
				scalar = new Scalar(0, 255, 255);
				break;
			default:
				break;
			}

//			Imgproc.drawContours(paper, questionCnts, q, scalar, 1);
//			Imgproc.drawContours(paper, questionCnts, q + 1, scalar, 1);
//			Imgproc.drawContours(paper, questionCnts, q + 2, scalar, 1);
//			Imgproc.drawContours(paper, questionCnts, q + 3, scalar, 1);
//			Imgproc.drawContours(paper, questionCnts, q + 4, scalar, 1);

			Question question = new Question();
			question.add(questionCnts.get(q));
			question.add(questionCnts.get(q + 1));
			question.add(questionCnts.get(q + 2));
			question.add(questionCnts.get(q + 3));
			question.add(questionCnts.get(q + 4));
			questions.add(question);

			index++;
		}
		write("output/" + nameFile + "_9_get_questions.jpg", paper);

		List<Integer> hm = new ArrayList<>();
		List<Integer> answers = getAnswerKey();
		int idx = 0;
		int correct = 0;
		for (Question question : questions) {
			int k = 0;
			int bubbleTotal = 0;
			for (int i = 0; i < question.cnts.size(); i++) {
				Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
				Imgproc.drawContours(mask, question.cnts, i, new Scalar(255, 255, 255), -1);
//				write("output/" + nameFile + "_10_" + i + ".jpg", mask);
				Core.bitwise_and(thresh, thresh, mask, mask);
				int total = Core.countNonZero(mask);
				if (total > bubbleTotal) {
					bubbleTotal = total;
					k = i;
				}
			}
			
			Scalar scalar = new Scalar(0, 0, 255);
			if (answers.get(idx++) == k) {
				scalar = new Scalar(0, 255, 0);
				correct += 1;
			}
			Imgproc.drawContours(paper, question.cnts, k, scalar, 2);
		}
		
		float score = (float) ((correct / 5.0) * 100);
		Imgproc.putText(paper, String.format("%.2f", score) + "%", new Point(10, 30),
				Core.FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(0, 0, 255), 2);
		write("output/" + nameFile + "_10_result.jpg", paper);

		sourceMat.release();
		gray.release();
		blurred.release();
		edged.release();
		warped.release();
		thresh.release();
		copy.release();
		copy2.release();
		copy3.release();
		copy4.release();
		copy5.release();
	}
	
	private static List<Integer> getAnswerKey()
	{
		List<Integer> answers = new ArrayList<>();
		answers.add(1);
		answers.add(4);
		answers.add(0);
		answers.add(3);
		answers.add(1);
		return answers;
	}

	private static void write(String output, Mat source) {
		System.out.println("Writefile: " + output);
		Imgcodecs.imwrite(output, source);
	}
}
