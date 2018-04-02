package BubbleSheetScanner;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import BubbleSheetScanner.Templates.AbstractTemplate;

public class DetectResource {

	private Mat blurred;

	private List<MatOfPoint> contourList;
	private Mat edged;
	private Mat gray;
	private String sourceName;
	private Mat src;
	private AbstractTemplate template;

	public DetectResource(String sourceName, AbstractTemplate template) {
		this.setSourceName(sourceName);
		this.setGray(new Mat());
		this.setBlurred(new Mat());
		this.setEdged(new Mat());
		this.setContourList(new ArrayList<>());
		this.setTemplate(template);
	}

	public void detect() {
		this.setSrc(Imgcodecs.imread(this.getSourceName()));

		Imgproc.cvtColor(this.getSrc(), this.getGray(), Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(this.getGray(), this.getBlurred(), new Size(5, 5), 0);
		Imgproc.Canny(this.getBlurred(), this.getEdged(), 75, 200);
		Utils.write("output/step1.jpg", this.getEdged());

		Imgproc.findContours(this.getEdged(), this.getContourList(), new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		this.setContourList(Utils.sortDescendingContours(this.getContourList()));
		this.getTemplate().findBiggestRectArea(this.getContourList());

		Mat copy2 = new Mat();
		gray.copyTo(copy2);
		Imgproc.drawContours(copy2, this.getContourList(), this.getTemplate().contourIdxMax, new Scalar(111), 3);
		Utils.write("output/step2.jpg", copy2);
	}

	public Mat getBlurred() {
		return blurred;
	}

	public List<MatOfPoint> getContourList() {
		return contourList;
	}

	public Mat getEdged() {
		return edged;
	}

	public Mat getGray() {
		return gray;
	}

	public String getSourceName() {
		return sourceName;
	}

	public Mat getSrc() {
		return src;
	}

	public AbstractTemplate getTemplate() {
		return template;
	}

	public void setBlurred(Mat blurred) {
		this.blurred = blurred;
	}

	public void setContourList(List<MatOfPoint> contourList) {
		this.contourList = contourList;
	}

	public void setEdged(Mat edged) {
		this.edged = edged;
	}

	public void setGray(Mat gray) {
		this.gray = gray;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public void setSrc(Mat src) {
		this.src = src;
	}

	public void setTemplate(AbstractTemplate template) {
		this.template = template;
	}

}
