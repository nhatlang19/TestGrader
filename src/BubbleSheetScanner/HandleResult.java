package BubbleSheetScanner;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class HandleResult {
	public static void handle(Mat thresh, Mat paper, List<Question> questions) {
		List<Integer> answers = Utils.getAnswerKey();
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
		Utils.write("output/9_result.jpg", paper);
	}
}
