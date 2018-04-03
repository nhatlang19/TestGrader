package BubbleSheetScanner;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class FilterQuestions {
	public static List<Question> get(Mat paper, List<MatOfPoint> questionCnts) {
		List<Question> questions = new ArrayList<>();
		int index = 0;
		for (int q = 0; q < questionCnts.size(); q += 5) {
			Scalar scalar = null;
//			switch (index) {
//			case 0:
//				scalar = new Scalar(0, 0, 255);
//				break;
//			case 1:
//				scalar = new Scalar(255, 0, 0);
//				break;
//			case 2:
//				scalar = new Scalar(0, 255, 0);
//				break;
//			case 3:
//				scalar = new Scalar(255, 0, 255);
//				break;
//			case 4:
//				scalar = new Scalar(0, 255, 255);
//				break;
//			default:
//				break;
//			}
//
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
		Utils.write("output/8_get_questions.jpg", paper);
		return questions;
	}
}
