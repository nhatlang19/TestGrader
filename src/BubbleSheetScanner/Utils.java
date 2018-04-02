package BubbleSheetScanner;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Utils {
	public static void write(String output, Mat source) {
		System.out.println("Writefile: " + output);
		Imgcodecs.imwrite(output, source);
	}
	
	public static List<MatOfPoint> sortDescendingContours(List<MatOfPoint> contourList) {
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
		
		return contourList;
	}
}
