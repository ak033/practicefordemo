import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CSVReader {
    public static void main(String[] args) {
        // Define the path to the CSV file
        String csvFile = "C:\\Users\\User\\Downloads\\MapSubway.csv";
        // Variable to hold the current line being read
        String line = "";
        // Define the delimiter used in the CSV file
        String csvSplitBy = "\t";
        // Create a HashMap to store the data from the CSV file
        Map<Integer, Map<String, String>> dataMap = new HashMap<>();
        // Variable to keep track of the current row number
        int rowNumber = 0;

        // Try-with-resources statement to ensure that the BufferedReader is closed when done
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Read the first line of the file to get the column headers
            String[] headers = br.readLine().split(csvSplitBy, -1);
            // Read the rest of the file line by line
            while ((line = br.readLine()) != null) {
                // Split the current line into an array of values using the defined delimiter
                String[] data = line.split(csvSplitBy, -1);
                // Create a nested HashMap to store the data for this row
                Map<String, String> rowData = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowData.put(headers[i], data[i]);
                }
                // Add the nested HashMap to the main HashMap using the row number as the key
                dataMap.put(rowNumber, rowData);
                // Increment the row number
                rowNumber++;
            }
        } catch (IOException e) {
            // Handle any exceptions that may occur when reading the file
            e.printStackTrace();
        }

        // Print out the contents of the HashMap
        for (Map.Entry<Integer, Map<String, String>> entry : dataMap.entrySet()) {
            System.out.println("Row " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
