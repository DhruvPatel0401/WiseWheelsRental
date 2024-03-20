package Uwindsor;
import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class SearchFrequencyMap {
	  private Map<String, Integer> searchFrequencyMap;
	    private File frequencyFile;
	    public SearchFrequencyMap() {
	        searchFrequencyMap = new TreeMap<>(); // Change to TreeMap for ordered storage
	        frequencyFile = new File("rc/main/resources/CarRentalData/SearchFrequency.txt");
	        if (!frequencyFile.exists()) {
	            try {
	                frequencyFile.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        loadFrequencyData();
	    }

	    private void loadFrequencyData() {
	        try (BufferedReader reader = new BufferedReader(new FileReader(frequencyFile))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(":");
	                searchFrequencyMap.put(parts[0], Integer.parseInt(parts[1]));
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public void updateSearchFrequency(String keyword) {
	        if (searchFrequencyMap.containsKey(keyword)) {
	            searchFrequencyMap.put(keyword, searchFrequencyMap.get(keyword) + 1);
	        } else {
	            searchFrequencyMap.put(keyword, 1);
	        }
	        saveFrequencyData();
	    }

	    public void deleteSearchFrequency(String keyword) {
	        searchFrequencyMap.remove(keyword);
	        saveFrequencyData();
	    }

	    private void saveFrequencyData() {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(frequencyFile))) {
	            for (Map.Entry<String, Integer> entry : searchFrequencyMap.entrySet()) {
	                writer.write(entry.getKey() + ":" + entry.getValue());
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public void displaySearchFrequency() {
	        System.out.println("Current Search Frequency:");
	        for (Map.Entry<String, Integer> entry : searchFrequencyMap.entrySet()) {
	            System.out.println(entry.getKey() + ": " + entry.getValue());
	        }
	    }

}
