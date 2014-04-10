package hw5;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Class for generating CSV files
 */
public class CSVGenerator {
	
	static void generateCSV(String outputFile, ArrayList<ArrayList<String>> data) {
		try {
			FileWriter writer = new FileWriter(outputFile);
			writer.append("Id");
			writer.append(",");
			writer.append("Category");
			writer.append('\n');
			for (ArrayList<String> row : data) {
				for (int i = 0; i < row.size(); i++) {
					writer.append(row.get(i));
					if (i == row.size() - 1) {
						writer.append('\n');
					} else {
						writer.append(',');
					}
				}
			}
		    writer.flush();
		    writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
