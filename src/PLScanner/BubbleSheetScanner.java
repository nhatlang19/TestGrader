package PLScanner;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import BubbleSheetScanner.Binarize;
import BubbleSheetScanner.DetectResource;
import BubbleSheetScanner.FilterCircle;
import BubbleSheetScanner.FilterQuestions;
import BubbleSheetScanner.HandleResult;
import BubbleSheetScanner.PerspectiveTransform;
import BubbleSheetScanner.Question;
import BubbleSheetScanner.Templates.AbstractTemplate;

public class BubbleSheetScanner {
	
	private DetectResource detectResource;
	
	
	public BubbleSheetScanner(String sourceFile, AbstractTemplate template) {
		detectResource = new DetectResource(sourceFile, template);
	}
	
	public void scanTopic() {
		this.detectResource.detect();
		
		MatOfPoint2f approxCurve = this.detectResource.getApproxCurveMax();
		Mat wrapped = PerspectiveTransform.transform(this.detectResource.getGray(), approxCurve);
		Mat paper = PerspectiveTransform.transform(this.detectResource.getSrc(), approxCurve);
		
		Mat thresh = Binarize.threshold(wrapped);
//		List<MatOfPoint> questionCnts = FilterCircle.filterCircleAnswers(thresh);
//		questionCnts = FilterCircle.sortContours(thresh, questionCnts);
//		
//		List<Question> questions = FilterQuestions.get(paper, questionCnts);
//		HandleResult.handle(thresh, paper, questions);
	}
	
	public void scanExamStudents() {
		this.detectResource.detect();
		
		MatOfPoint2f approxCurve = this.detectResource.getApproxCurveMax();
		Mat wrapped = PerspectiveTransform.transform(this.detectResource.getGray(), approxCurve);
		Mat paper = PerspectiveTransform.transform(this.detectResource.getSrc(), approxCurve);
		
		Mat thresh = Binarize.threshold(wrapped);
		List<MatOfPoint> questionCnts = FilterCircle.filterCircleTopic(thresh);
		questionCnts = FilterCircle.sortContours(thresh, questionCnts);
		
		List<Question> questions = FilterQuestions.getTopic(paper, questionCnts);
	}
}
