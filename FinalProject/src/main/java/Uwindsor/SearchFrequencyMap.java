package Uwindsor;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SearchFrequencyMap {
	private Map<String, Integer> HistroyDataMap; // Map to keep track of search terms and their frequency
    private File dataReadToBeFile; // A file containing search history information

    // Constructor to import current data from file and initialise the map and file
    public SearchFrequencyMap(String pathToFile) {
        HistroyDataMap = new TreeMap<>(); // Switch to a TreeMap to have organised storage
        
        dataReadToBeFile = new File(pathToFile); //Set up the file object first.
        if (!dataReadToBeFile.exists()) { // If the file isn't there, create it.
            try {
                dataReadToBeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AddDataToTheFile(); // Import current data from the file.
    }

    // to use the file to import current data into the map
    private void AddDataToTheFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataReadToBeFile))) {
            String EachLineInFile;
            while ((EachLineInFile = reader.readLine()) != null) { // Go over each line in the file.
                String[] SingleWord = EachLineInFile.split(":"); // Divide every line by ":"
                HistroyDataMap.put(SingleWord[0], Integer.parseInt(SingleWord[1])); // Keep track of a keyword's frequency in the map.
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // to modify a keyword's frequency in a map and save it to a file
    public void dataUpdateInTheFile(String searchingKeyword) {
        if (HistroyDataMap.containsKey(searchingKeyword)) { // If the search term is already present on the map
            HistroyDataMap.put(searchingKeyword, HistroyDataMap.get(searchingKeyword) + 1); // Increase the frequency of it
        } else { 
            HistroyDataMap.put(searchingKeyword, 1); // Include it with frequency 1 on the map.
        }
        SaveDataToFile(); //Update the file with the most recent data.
    }

    //save updated data to the file and remove a term from the map
    public void datadeleteinfile(String keyworddata) {
        HistroyDataMap.remove(keyworddata); //Take the term off of the map.
        SaveDataToFile(); // Update the file with the most recent data.
    }

    // Technique for mapping data to be saved in a file
    private void SaveDataToFile() {
        try (BufferedWriter newfiledataText = new BufferedWriter(new FileWriter(dataReadToBeFile))) {
            for (Map.Entry<String, Integer> entry : HistroyDataMap.entrySet()) { // Go over the map entries one by one.
                newfiledataText.write(entry.getKey() + ":" + entry.getValue()); // Add the term and its occurrences to the document.
                newfiledataText.newLine(); 
            }
        } catch (IOException ev) {
            ev.printStackTrace();
        }
    }

    // to present search history information
    public void filedataDisplaying() {
        System.out.println("Search History:");
        for (Map.Entry<String, Integer> entry : HistroyDataMap.entrySet()) { // Go over the map entries one by one.
            System.out.println(entry.getKey() + ": " + entry.getValue()); // Print keyword frequency
        }
    }
}
