import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;

import BubbleSheetScanner.DetectResource;
import BubbleSheetScanner.Templates.Template50;
import PLScanner.BubbleSheetScanner;

public class Hello2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String nameFile = "test3";
		String sourceFileName = "images/" + nameFile + ".png";
		
		BubbleSheetScanner scanner = new BubbleSheetScanner(sourceFileName, new Template50());
		scanner.scan();
	}
}
