package Resources;

import java.util.ArrayList;
import java.util.List;

public class algorithm {

    // Linear Search
    public static String[][] linearSearch(String[][] data, String target, int filterIndex) {
        List<String[]> results = new ArrayList<>();
        for (int i = 1; i < data.length; i++) { // Skipping header
            if (data[i][filterIndex].equalsIgnoreCase(target)) {
                results.add(data[i]);
            }
        }
        return results.toArray(new String[0][]);
    }

    // Binary Search
    public static String[][] binarySearch(String[][] data, String target, int filterIndex) {
        List<String[]> results = new ArrayList<>();
        int left = 0;
        int right = data.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = data[mid][filterIndex].compareToIgnoreCase(target);

            if (cmp == 0) {
                // Found, add all matching rows
                int start = mid;
                while (start >= 0 && data[start][filterIndex].equalsIgnoreCase(target)) {
                    results.add(data[start]);
                    start--;
                }
                int end = mid + 1;
                while (end < data.length && data[end][filterIndex].equalsIgnoreCase(target)) {
                    results.add(data[end]);
                    end++;
                }
                break;
            }
            if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return results.toArray(new String[0][]);
    }
}
