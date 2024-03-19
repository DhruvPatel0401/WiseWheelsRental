### Project Overview

This project is a Java application designed to fetch car rental information from a specific website based on user input such as location, start date, and end date. It consists of three main classes:

1. `Main.java`: Handles user interaction and input validation.
2. `WebCrawler.java`: Responsible for crawling the car rental website, extracting relevant information, and storing it in a JSON file.
3. `WordCompletion.java`: Provides functionality for finding nearest words to a given input using edit distance.

### How to Run the Project

1. Clone the repository or download the source code.
2. Ensure you have Java and Maven installed on your system.
3. Set up WebDriver for Selenium by placing `chromedriver.exe` in the `src/main/resources/` directory (for Windows).
4. Compile and build the project using Maven: `mvn clean install`.
5. Run the `Main.java` file to interact with the application.

### Usage Instructions

1. Upon running `Main.java`, the user is prompted to enter a pick-up location.
2. The user is then asked to input start and end dates for the car rental.
3. The application fetches car rental information from the website based on the provided inputs.
4. The extracted data is stored in the `output.json` file in JSON format.

### Dependencies

- **Selenium WebDriver:** Used for web scraping car rental data.
- **JUnit:** Framework for writing and running tests (not included in the provided code).
- **JSON:** Library for handling JSON data.

### Note

- This README provides an overview of the project structure and usage instructions. Additional documentation or inline comments within the code may provide further insights into specific implementation details or functionalities.
