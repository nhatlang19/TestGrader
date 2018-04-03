import org.opencv.core.Core;

import BubbleSheetScanner.Utils;
import BubbleSheetScanner.Templates.Template50;
import PLScanner.BubbleSheetScanner;

public class ReadMaDe {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Utils.cleanOutput();

		String nameFile = "test4";
		String sourceFileName = "images/" + nameFile + ".jpg";

		BubbleSheetScanner scanner = new BubbleSheetScanner(sourceFileName, new Template50());
		scanner.scanExamStudents();
	}
}
