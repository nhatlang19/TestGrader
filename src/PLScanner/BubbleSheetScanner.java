package PLScanner;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

import BubbleSheetScanner.Binarize;
import BubbleSheetScanner.DetectResource;
import BubbleSheetScanner.PerspectiveTransform;
import BubbleSheetScanner.Templates.AbstractTemplate;

public class BubbleSheetScanner {
	
	private DetectResource detectResource;
	
	
	public BubbleSheetScanner(String sourceFile, AbstractTemplate template) {
		detectResource = new DetectResource(sourceFile, template);
	}
	
	public void scan() {
		this.detectResource.detect();
		
		Mat gray = this.detectResource.getGray();
		MatOfPoint2f approxCurve = this.detectResource.getTemplate().approxCurveMax;
		PerspectiveTransform perspectiveTransform = new PerspectiveTransform(gray, approxCurve);
		perspectiveTransform.transform();
		
		Binarize binarize = new Binarize(perspectiveTransform.getWarped());
		binarize.threshold();
	}
}
