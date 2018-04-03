import org.opencv.core.Core;
import BubbleSheetScanner.Templates.Template50;
import PLScanner.BubbleSheetScanner;

public class TestGraderCustomize {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String nameFile = "test3";
		String sourceFileName = "images/" + nameFile + ".png";
		
		BubbleSheetScanner scanner = new BubbleSheetScanner(sourceFileName, new Template50());
		scanner.scanTopic();
	}
}
