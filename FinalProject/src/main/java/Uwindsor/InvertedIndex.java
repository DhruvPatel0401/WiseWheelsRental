package Uwindsor;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    // a list of terms that won't be indexed
    private static final Set<String> EXCLUDED_WORDS = new HashSet<>(Arrays.asList(
            "is", "an", "the", "Car", "Name", "Type", "Max", "Passengers", "Size", "Price", 
            "per", "Day", "Total", "Vendor", "Rating", "No", "no", "ratings", "Available", "at"
    ));
    
    // Paths for the output files
    private static final String outputFilePath = "src/main/resources/CarRentalData/InvertedIndexTable.txt";
    private static final String outputFilePathData = "src/main/resources/CarRentalData";

    // Recursive data file indexing technique in a directory
    public void DataIndexFile(File directory) {
        if (!directory.isDirectory()) {
            return;
        }

        Map<String, Set<String>> invertedTableData = new HashMap<>();

        for (File infoData : directory.listFiles()) {
            if (infoData.isDirectory()) {
                DataIndexFile(infoData);
            } else {
                // Ignore files with particular names.
                if (skiptheFileinthefolder(infoData)) {
                    continue;
                }
                dataindexfileInfo(infoData, invertedTableData);
            }
        }

        refenceindexfile(invertedTableData, outputFilePath);
    }

    // Ignore files with particular names.
    private boolean skiptheFileinthefolder(File file) {
        String fileInfoNameData = file.getName();
        return fileInfoNameData.equals("InvertedIndexTable.txt") || fileInfoNameData.equals("SearchKeywordHistory.txt");
    }

    // to index information from a file
    private void dataindexfileInfo(File file, Map<String, Set<String>> invertedIndex) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (!EXCLUDED_WORDS.contains(word)) {
                        invertedIndex.computeIfAbsent(word, k -> new HashSet<>()).add(file.getName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Technique for doing a keyword-based information search
    public Set<String> seachInformationData(String keyword, String inputFilePath) {
        Map<String, Set<String>> invertedtableInfo = datareadfromFileData(inputFilePath);
        String datatolowercase = keyword.toLowerCase(Locale.ENGLISH); // Convert keyword to lowercase
        for (Map.Entry<String, Set<String>> entry : invertedtableInfo.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(datatolowercase)) { // Case-insensitive comparison
                return entry.getValue(); // Return files corresponding to matched keyword
            }
        }
        return Collections.emptySet();
    }

    // A technique for carrying out several searches using a variety of keywords
    public Set<String> multipledatasearch(String[] keywords) {
        Map<String, Set<String>> invtdata = datareadfromFileData(outputFilePath);
        Set<String> finaldataresultfile = new HashSet<>();

        for (String dataiteration : keywords) {
            String datatolowercase = dataiteration.toLowerCase(Locale.ENGLISH); 
            for (Map.Entry<String, Set<String>> entry : invtdata.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(datatolowercase)) { 
                    finaldataresultfile.addAll(entry.getValue()); // Add the files that match the entered keyword.
                    break;
                }
            }
        }
        return finaldataresultfile;
    }

    //indexed data is written to a file
    private void refenceindexfile(Map<String, Set<String>> invertedIndex, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
                String keyword = entry.getKey();
                String files = String.join(",", entry.getValue()); // Integrate filenames using commas
                writer.println(keyword + ":" + files); // Use a colon to divide the filename and the keyword.
            }
        } catch (IOException eu) {
            eu.printStackTrace();
        }
    }

    // open a file with indexed data
    public static Map<String, Set<String>> datareadfromFileData(String inputFilePath) {
        Map<String, Set<String>> invertedTableData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] eachDataInfile = line.split(":"); 
                String eachWordInfile = eachDataInfile[0];
                String[] allthefilesInfolder = eachDataInfile[1].split(","); // filenames with commas
                Set<String> filesData = new HashSet<>(Arrays.asList(allthefilesInfolder));
                invertedTableData.put(eachWordInfile, filesData);
            }
        } catch (IOException eb) {
            eb.printStackTrace();
        }
        return invertedTableData;
    }

    // print content before conducting a search
    public static void printBeforeSearchingData(String[] keywords) {
        Map<String, Set<String>> invertedData = datareadfromFileData(outputFilePath);

        for (String keyword : keywords) {
            String lowercaseKeyword = keyword.toLowerCase(Locale.ENGLISH); // Lowercase the keyword
            for (Map.Entry<String, Set<String>> entry : invertedData.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(lowercaseKeyword)) { //Comparison without regard to case
                    Set<String> filesData = entry.getValue();
                    for (String fileName : filesData) {
                        dataShowByPrinting(outputFilePathData + "/" + fileName); // Before printing, add the file path.
                    }
                    break;
                }
            }
        }
    }

    // read data from a file and print it
    public static void dataShowByPrinting(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            System.out.println("Content of file: " + filePath);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("----------------------------------");
        } catch (IOException ey) {
            ey.printStackTrace();
        }
    }
}
