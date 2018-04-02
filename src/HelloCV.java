import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HelloCV {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);

		String sourceFileName = "images/test3.png";
		// read images
		Mat sourceMat = Imgcodecs.imread(sourceFileName, Imgcodecs.IMREAD_GRAYSCALE);
		System.out.println("width, height = " + sourceMat.width() + ", " + sourceMat.height());
		
		// step 1: Perform image preprocessing to make the image black & white (binarization)
		Imgproc.GaussianBlur(sourceMat, sourceMat, new Size(1, 1), 0);
		Imgproc.adaptiveThreshold(sourceMat, sourceMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,75,10);
		Core.bitwise_not(sourceMat, sourceMat);
		write("output/test6_step1.jpg", sourceMat);
		
		// step 2: Use hough transform to find the lines in the image
		Mat img2 = new Mat();
		Mat lines = new Mat();
		Imgproc.cvtColor(sourceMat, img2, Imgproc.COLOR_GRAY2RGB);
		Imgproc.HoughLinesP(sourceMat, lines, 1, Math.PI/180, 80, 40, 10);
		
		System.out.println(lines.cols());
		for (int i = 0; i < lines.cols(); i++) {
			double[] val = lines.get(0, i);
			System.out.println(val);
			if(val != null) {
				Imgproc.line(img2, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
			}
			
		}
		Imgcodecs.imwrite("output/test6_step2.jpg", img2);
		
		// save image
		//Imgcodecs.imwrite("output/test6_step1.jpg", sourceMat);
	}
	
	private static void write(String output, Mat source) {
		System.out.println("Writefile: " + output);
		Imgcodecs.imwrite(output, source);
	}
}
