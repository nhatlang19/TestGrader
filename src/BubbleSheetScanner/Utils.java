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
	
	public static void sortContoures(List<MatOfPoint> cnts, String method)
	{
//		def sort_contours(cnts, method="left-to-right"):
//			# initialize the reverse flag and sort index
//			reverse = False
//			i = 0
//		 
//			# handle if we need to sort in reverse
//			if method == "right-to-left" or method == "bottom-to-top":
//				reverse = True
//		 
//			# handle if we are sorting against the y-coordinate rather than
//			# the x-coordinate of the bounding box
//			if method == "top-to-bottom" or method == "bottom-to-top":
//				i = 1
//		 
//			# construct the list of bounding boxes and sort them from top to
//			# bottom
//			boundingBoxes = [cv2.boundingRect(c) for c in cnts]
//			(cnts, boundingBoxes) = zip(*sorted(zip(cnts, boundingBoxes),
//				key=lambda b:b[1][i], reverse=reverse))
//		 
//			# return the list of sorted contours and bounding boxes
//			return (cnts, boundingBoxes)
	}
}
