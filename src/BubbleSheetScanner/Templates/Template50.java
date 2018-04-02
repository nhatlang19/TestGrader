package BubbleSheetScanner.Templates;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class Template50 extends AbstractTemplate{
	
	
	
	@Override
	public void findAllRectArea(List<MatOfPoint> contourList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void findBiggestRectArea(List<MatOfPoint> contourList) {		
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
	}
}
