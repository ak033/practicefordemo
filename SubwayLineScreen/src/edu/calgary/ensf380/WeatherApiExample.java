package edu.calgary.ensf380;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherApiExample {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: WeatherApiExample <city_name>");
            return;
        }

        String apiKey = "2aebaa6b7225dcfebca9b1a226888618"; // Replace with your actual API key
        String cityName = args[0];

        // Step 1: Geocoding API call to get coordinates
        String geocodingUrl = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&appid=%s", cityName, apiKey);

        try {
            URL url = new URL(geocodingUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                String apiResponse = response.toString();
                String latRegex = "\"lat\":(\\d+\\.\\d+)";
                String lonRegex = "\"lon\":(-?\\d+\\.\\d+)";
                String timezonePattern = "\"timezone\":(-?\\d+),";

                Pattern latPattern = Pattern.compile(latRegex);
                Pattern lonPattern = Pattern.compile(lonRegex);
                Pattern timezoneRegex = Pattern.compile(timezonePattern);

                Matcher latMatcher = latPattern.matcher(apiResponse);
                Matcher lonMatcher = lonPattern.matcher(apiResponse);

                String latitude = "";
                String longitude = "";

                if (latMatcher.find() && lonMatcher.find()) {
                    latitude = latMatcher.group(1);
                    longitude = lonMatcher.group(1);
                } else {
                    System.out.println("Latitude and/or Longitude not found.");
                    connection.disconnect();
                    return;
                }

                // Step 2: Weather API call using obtained coordinates
                String weatherUrl = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", latitude, longitude, apiKey);

                URL weatherApiUrl = new URL(weatherUrl);
                HttpURLConnection weatherConnection = (HttpURLConnection) weatherApiUrl.openConnection();
                weatherConnection.setRequestMethod("GET");

                int weatherResponseCode = weatherConnection.getResponseCode();
                if (weatherResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
                    StringBuilder weatherResponse = new StringBuilder();

                    while ((inputLine = weatherReader.readLine()) != null) {
                        weatherResponse.append(inputLine);
                    }
                    weatherReader.close();

                    String weatherApiResponse = weatherResponse.toString();
                    String weatherPattern = "\"weather\":\\[(.+?)\\],";
                    String mainPattern = "\"main\":\\{[^\\}]*\\}";
                    String timePattern = "\"dt\":(\\d+),";
                    
                    
                    Pattern weatherRegex = Pattern.compile(weatherPattern);
                    Pattern mainRegex = Pattern.compile(mainPattern);
                    Pattern timeRegex = Pattern.compile(timePattern);

                    Matcher weatherMatcher = weatherRegex.matcher(weatherApiResponse);
                    Matcher mainMatcher = mainRegex.matcher(weatherApiResponse);
                    Matcher timeMatcher = timeRegex.matcher(weatherApiResponse);
                    Matcher timezoneMatcher = timezoneRegex.matcher(weatherApiResponse);

                    if (weatherMatcher.find() && mainMatcher.find() && timeMatcher.find()&& timezoneMatcher.find()) {
                        String weatherSection = weatherMatcher.group(1);
                        String mainSection = mainMatcher.group();
                        long unixTime = Long.parseLong(timeMatcher.group(1));
                       int timezoneOffset =  Integer.parseInt(timezoneMatcher.group(1));
                        
                        // Convert temperature from Kelvin to Celsius
                        mainSection = mainSection.replace("\"temp\":", "\"tempCelsius\":");
                        mainSection = mainSection.replace("\"feels_like\":", "\"feels_likeCelsius\":");
                        mainSection = mainSection.replace("\"temp_min\":", "\"temp_minCelsius\":");
                        mainSection = mainSection.replace("\"temp_max\":", "\"temp_maxCelsius\":");

                        Pattern tempPattern = Pattern.compile("\"tempCelsius\":(\\d+\\.?\\d*)");
                        Matcher tempMatcher = tempPattern.matcher(mainSection);
                        
                        String tempCelsiusPattern = "\"tempCelsius\":(-?\\d+\\.?\\d*)";
                        Pattern tempCelsiusRegex = Pattern.compile(tempCelsiusPattern);
                        Matcher tempCelsiusMatcher = tempCelsiusRegex.matcher(mainSection);

                       
                        if (tempCelsiusMatcher.find()) {
                            double tempCelsius = Double.parseDouble(tempCelsiusMatcher.group(1))-273.15;
                            System.out.println("Weather: " + Math.round(tempCelsius) + " degrees");
                        } else {
                            System.out.println("Temperature not found.");
                        }

                        // Convert Unix timestamp to formatted date and time with AM/PM format
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT" + (timezoneOffset >= 0 ? "+" : "") + timezoneOffset / 3600));                        String formattedTime = sdf.format(new Date(unixTime * 1000));

//                        System.out.println("Weather: " + weatherSection);
                        System.out.println("Time: " + formattedTime);
                    } else {
                        System.out.println("Weather or Main section not found.");
                    }
                } else {
                    System.out.println("Weather API request failed with response code: " + weatherResponseCode);
                }

                weatherConnection.disconnect();
            } else {
                System.out.println("Geocoding API request failed with response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




