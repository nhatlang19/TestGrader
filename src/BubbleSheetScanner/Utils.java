package BubbleSheetScanner;

import java.io.File;
import java.util.ArrayList;
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
	
	public static List<Integer> getAnswerKey()
	{
		List<Integer> answers = new ArrayList<>();
		answers.add(1);
		answers.add(4);
		answers.add(0);
		answers.add(3);
		answers.add(1);
		return answers;
	}
	
	public static void cleanOutput() {
		File directory = new File("output/");

		// Get all files in directory

		File[] files = directory.listFiles();
		for (File file : files) {
			// Delete each file

			if (!file.delete()) {
				// Failed to delete file
				System.out.println("Failed to delete " + file);
			}
		}
	}
}
