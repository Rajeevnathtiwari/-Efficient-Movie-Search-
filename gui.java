


package Resources;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class gui {

    private static final String CSV_FILE_PATH = "src/resources/imdb_top_1000_filled.csv";
    private static String[][] dataset;
    private static String selectedFilter = "Series_Title"; // Default filter

    public static void initializeAndShowGUI() {
        // Load data from CSV
        dataset = loadCSV(CSV_FILE_PATH);
        if (dataset == null || dataset.length == 0) {
            JOptionPane.showMessageDialog(null, "No data found in the CSV file.");
            return;
        }

        // Create and display the GUI
        SwingUtilities.invokeLater(gui::createAndShowGUI);
    }

    // Method to load data from CSV
    public static String[][] loadCSV(String filePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            return csvReader.readAll().toArray(new String[0][]);
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        } catch (com.opencsv.exceptions.CsvException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to create and show the GUI
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Movie Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());

        JButton yearButton = new JButton("Released_Year");
        JButton certificateButton = new JButton("Certificate");
        JButton runtimeButton = new JButton("Runtime");
        JButton ratingButton = new JButton("IMDB_Rating");
        JButton refreshButton = new JButton("Refresh"); // Added Refresh button

        filterPanel.add(yearButton);
        filterPanel.add(certificateButton);
        filterPanel.add(runtimeButton);
        filterPanel.add(ratingButton);
        filterPanel.add(refreshButton); // Add Refresh button to the panel

        frame.add(filterPanel, BorderLayout.NORTH);

        // Search panel (hidden initially)
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton linearSearchButton = new JButton("Linear Search");
        JButton binarySearchButton = new JButton("Binary Search");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(linearSearchButton);
        searchPanel.add(binarySearchButton);

        // Hide search panel initially
        searchPanel.setVisible(false);
        frame.add(searchPanel, BorderLayout.CENTER);

        // Table to display results
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Add column headers
        tableModel.addColumn("Poster_Link");
        tableModel.addColumn("Series_Title");
        tableModel.addColumn("Released_Year");
        tableModel.addColumn("Certificate");
        tableModel.addColumn("Runtime");
        tableModel.addColumn("Genre");
        tableModel.addColumn("IMDB_Rating");
        tableModel.addColumn("Overview");
        tableModel.addColumn("Meta_score");
        tableModel.addColumn("Director");
        tableModel.addColumn("Star1");
        tableModel.addColumn("Star2");
        tableModel.addColumn("Star3");
        tableModel.addColumn("Star4");
        tableModel.addColumn("No_of_Votes");
        tableModel.addColumn("Gross");

        // Set custom renderer for Poster_Link
        table.getColumnModel().getColumn(0).setCellRenderer(new LinkRenderer());

        // Initially display all data
        if (dataset != null) {
            updateTable(tableModel, dataset);
        } else {
            JOptionPane.showMessageDialog(null, "Dataset is null.");
        }

        // Action listeners for filter buttons
        ActionListener filterListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                selectedFilter = source.getText();
                searchPanel.setVisible(true);
                searchField.setText(""); // Clear search field
                updateTable(tableModel, dataset); // Show all data
            }
        };

        yearButton.addActionListener(filterListener);
        certificateButton.addActionListener(filterListener);
        runtimeButton.addActionListener(filterListener);
        ratingButton.addActionListener(filterListener);

        // Action listener for Refresh button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPanel.setVisible(false); // Hide search panel
                searchField.setText(""); // Clear search field
                updateTable(tableModel, dataset); // Show all data
            }
        });

        // Action listeners for search buttons
        linearSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (!query.isEmpty()) {
                    int filterIndex = getFilterIndex(selectedFilter);
                    System.out.println("Linear Search for: " + query + " in column index: " + filterIndex);
                    String[][] results = algorithm.linearSearch(dataset, query, filterIndex);
                    System.out.println("Results found: " + results.length);
                    updateTable(tableModel, results);
                }
            }
        });

        binarySearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (!query.isEmpty()) {
                    int filterIndex = getFilterIndex(selectedFilter);
                    System.out.println("Binary Search for: " + query + " in column index: " + filterIndex);
                    String[][] sortedDataset = Arrays.copyOf(dataset, dataset.length);
                    Arrays.sort(sortedDataset, (a, b) -> a[filterIndex].trim().compareToIgnoreCase(b[filterIndex]));
                    String[][] results = algorithm.binarySearch(sortedDataset, query, filterIndex);
                    System.out.println("Results found: " + results.length);
                    updateTable(tableModel, results);
                }
            }
        });

        // Add mouse listener to handle clicks on poster links
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column == 0) { // Assuming Poster_Link is in the first column
                    String link = (String) table.getValueAt(row, column);
                    showPoster(link);
                }
            }
        });

        // Action listener to handle search field changes
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    updateTable(tableModel, dataset); // Show all data if search field is empty
                }
            }
        });

        frame.setVisible(true);
    }

    // Method to update table with search results
    public static void updateTable(DefaultTableModel tableModel, String[][] data) {
        if (data != null) {
            tableModel.setRowCount(0); // Clear existing rows
            for (String[] row : data) {
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No data to display.");
        }
    }

    // Helper method to get column index based on the filter
    public static int getFilterIndex(String filter) {
        switch (filter) {
            case "Released_Year":
                return 2;
            case "Certificate":
                return 3;
            case "Runtime":
                return 4;
            case "IMDB_Rating":
                return 6;
            default:
                return 1; // Default to Series_Title
        }
    }

    // Method to display poster image
    public static void showPoster(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage img = ImageIO.read(url);
            ImageIcon icon = new ImageIcon(img);
            JLabel label = new JLabel(icon);
            JScrollPane scrollPane = new JScrollPane(label);
            JFrame frame = new JFrame("Poster");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(600, 800);
            frame.add(scrollPane);
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// LinkRenderer class to make Poster_Link clickable
class LinkRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof String) {
            String link = (String) value;
            c.setForeground(Color.BLUE);
            c.setCursor(new Cursor(Cursor.HAND_CURSOR));
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        }
        return c;
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof String) {
            setText("<html><u>" + value + "</u></html>");
        } else {
            setText(value.toString());
        }
    }
}


