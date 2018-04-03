package BubbleSheetScanner;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint;

public class Question {
	public List<MatOfPoint> cnts = new ArrayList<>();

	public void add(MatOfPoint point) {
		cnts.add(point);
	}
}
