
package Resources;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class data {
    private static final String CSV_FILE_PATH = "src/resources/imdb_top_1000.csv";
    private static final String OUTPUT_CSV_FILE_PATH = "src/resources/imdb_top_1000_filled.csv";

    public static void main(String[] args) {
        List<String[]> data = loadCSV(CSV_FILE_PATH);
        if (data != null && !data.isEmpty()) {
            String[] columnNames = data.get(0);

            // Get initial missing data
            System.out.println("Initial Missing Data Analysis:");
            Map<Integer, Integer> initialMissingCounts = getMissingCounts(data);
            printMissingDataTable(columnNames, initialMissingCounts);

            // Find frequent values
            Map<Integer, String> frequentValues = findFrequentValues(data);

            // Replace missing values
            replaceMissingValues(data, frequentValues);

            // Get updated missing data
            System.out.println("\nUpdated Missing Data Analysis:");
            Map<Integer, Integer> updatedMissingCounts = getMissingCounts(data);
            printMissingDataTable(columnNames, updatedMissingCounts);

            // Save the updated CSV
            saveCSV(data, OUTPUT_CSV_FILE_PATH);
        }
    }

    // Method to load the CSV file
    public static List<String[]> loadCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                data.add(nextRecord);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Method to find frequent values in each column
    public static Map<Integer, String> findFrequentValues(List<String[]> data) {
        Map<Integer, String> frequentValues = new HashMap<>();
        if (data.isEmpty()) return frequentValues;

        // Collect column data
        String[] columnNames = data.get(0);
        Map<Integer, List<String>> columnData = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            List<String> values = new ArrayList<>();
            for (int j = 1; j < data.size(); j++) {
                String value = data.get(j)[i];
                if (value != null && !value.trim().isEmpty()) {
                    values.add(value);
                }
            }
            columnData.put(i, values);
        }

        // Find the most frequent value in each column
        for (Map.Entry<Integer, List<String>> entry : columnData.entrySet()) {
            int columnIndex = entry.getKey();
            List<String> values = entry.getValue();
            String frequentValue = values.stream()
                    .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            frequentValues.put(columnIndex, frequentValue);
        }

        return frequentValues;
    }

    // Method to get missing counts in each column
    public static Map<Integer, Integer> getMissingCounts(List<String[]> data) {
        Map<Integer, Integer> missingCounts = new HashMap<>();
        if (data.isEmpty()) return missingCounts;

        String[] columnNames = data.get(0);
        int[] missingCount = new int[columnNames.length];

        for (int i = 1; i < data.size(); i++) { // Start from 1 to skip the header row
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null || row[j].trim().isEmpty()) {
                    missingCount[j]++;
                }
            }
        }

        // Store missing counts in a map
        for (int i = 0; i < columnNames.length; i++) {
            missingCounts.put(i, missingCount[i]);
        }

        return missingCounts;
    }

    // Method to print missing data in a tabular format without frequent values
    public static void printMissingDataTable(String[] columnNames, Map<Integer, Integer> missingCounts) {
        System.out.printf("%-30s%-20s%n", "Column Name", "Missing Values");
        System.out.println("---------------------------------------------------------------");

        for (int i = 0; i < columnNames.length; i++) {
            System.out.printf("%-30s%-20d%n", columnNames[i], missingCounts.getOrDefault(i, 0));
        }
    }

    // Method to replace missing values
    public static void replaceMissingValues(List<String[]> data, Map<Integer, String> frequentValues) {
        if (data.isEmpty()) return;

        for (int i = 1; i < data.size(); i++) { // Start from 1 to skip the header row
            String[] row = data.get(i);
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null || row[j].trim().isEmpty()) {
                    row[j] = frequentValues.getOrDefault(j, "N/A");
                }
            }
        }
    }

    // Method to save the updated CSV
    public static void saveCSV(List<String[]> data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

