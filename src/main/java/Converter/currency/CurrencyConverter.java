package Converter.currency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CurrencyConverter {
    private static final String API_URL = "https://api.apilayer.com/exchangerates_data/latest";
    private static final String API_KEY = "https://api.exchangerate.host/convert?from=USD&to=EUR"; 
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private static Map<String, String> favoriteCurrencies = new HashMap<>();

    public static void main(String[] args) {
        loadExchangeRates();

        while (true) {
            System.out.println("\nCurrency Converter Menu:");
            System.out.println("1. Convert Currency");
            System.out.println("2. Show Favorite Currencies");
            System.out.println("3. Add Favorite Currency");
            System.out.println("4. Update Favorite Currency");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        convertCurrency();
                        break;
                    case 2:
                        showFavoriteCurrencies();
                        break;
                    case 3:
                        addFavoriteCurrency();
                        break;
                    case 4:
                        updateFavoriteCurrency();
                        break;
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void loadExchangeRates() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("apikey", API_KEY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
            JSONObject rates = (JSONObject) jsonObject.get("rates");

            for (Object key : rates.keySet()) {
                exchangeRates.put(key.toString(), (Double) rates.get(key));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void convertCurrency() {
        System.out.print("Enter the amount (in USD): ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            double amount = Double.parseDouble(reader.readLine());

            System.out.print("Enter the target currency code (e.g., EUR): ");
            String targetCurrency = reader.readLine().toUpperCase();

            if (exchangeRates.containsKey(targetCurrency)) {
                double exchangeRate = exchangeRates.get(targetCurrency);
                double convertedAmount = amount * exchangeRate;
                System.out.println("Converted amount: " + convertedAmount + " " + targetCurrency);
            } else {
                System.out.println("Invalid currency code.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void showFavoriteCurrencies() {
        if (favoriteCurrencies.isEmpty()) {
            System.out.println("No favorite currencies added.");
        } else {
            System.out.println("Favorite Currencies:");
            for (String code : favoriteCurrencies.keySet()) {
                System.out.println(code + " - " + favoriteCurrencies.get(code));
            }
        }
    }

    private static void addFavoriteCurrency() {
        System.out.print("Enter the currency code (e.g., EUR): ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String currencyCode = reader.readLine().toUpperCase();

            if (exchangeRates.containsKey(currencyCode)) {
                System.out.print("Enter a description for this currency: ");
                String description = reader.readLine();
                favoriteCurrencies.put(currencyCode, description);
                System.out.println(currencyCode + " has been added to your favorites.");
            } else {
                System.out.println("Invalid currency code.");
            }
        } catch (IOException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void updateFavoriteCurrency() {
        System.out.print("Enter the currency code to update (e.g., EUR): ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String currencyCode = reader.readLine().toUpperCase();

            if (favoriteCurrencies.containsKey(currencyCode)) {
                System.out.print("Enter a new description for this currency: ");
                String description = reader.readLine();
                favoriteCurrencies.put(currencyCode, description);
                System.out.println(currencyCode + " has been updated.");
            } else {
                System.out.println("Currency code not found in favorites.");
            }
        } catch (IOException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }
}

