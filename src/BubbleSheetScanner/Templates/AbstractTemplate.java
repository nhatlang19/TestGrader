package BubbleSheetScanner.Templates;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

abstract public class AbstractTemplate {
	private int contourIdxMax;
	private MatOfPoint2f approxCurveMax = new MatOfPoint2f();
	
	public int getContourIdxMax() {
		return contourIdxMax;
	}

	public void setContourIdxMax(int contourIdxMax) {
		this.contourIdxMax = contourIdxMax;
	}

	public MatOfPoint2f getApproxCurveMax() {
		return approxCurveMax;
	}

	public void setApproxCurveMax(MatOfPoint2f approxCurveMax) {
		this.approxCurveMax = approxCurveMax;
	}

	public abstract void findAllRectArea(List<MatOfPoint> contourList);
	
	public abstract void findBiggestRectArea(List<MatOfPoint> contourList);
}
