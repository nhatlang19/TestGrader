package BubbleSheetScanner.Templates;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

abstract public class AbstractTemplate {
	public int contourIdxMax;
	public MatOfPoint2f approxCurveMax = new MatOfPoint2f();

	public abstract void findAllRectArea(List<MatOfPoint> contourList);
	
	public abstract void findBiggestRectArea(List<MatOfPoint> contourList);
}
