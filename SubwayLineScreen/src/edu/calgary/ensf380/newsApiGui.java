import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONObject;

public class newsApiGui {
    public static void main(String[] args) {
        // Check if a city name was provided as a command-line argument
        if (args.length < 1) {
            System.err.println("Usage: java NewsAPIExample <city>");
            System.exit(1);
        }

        // Get the city name from the command-line argument
        String city = args[0];
        String startDate = args[1];
        String endDate = args[2];
        String sortOrder = args[3];

        // Create and show the NewsGui
//        NewsFetcher newsFetcher = new NewsFetcher(city);
        NewsFetcher newsFetcher = new NewsFetcher(city, startDate, endDate, sortOrder);

        new NewsGui(newsFetcher);
    }

    public static class NewsGui extends JFrame {
        private static final long serialVersionUID = 1L;

        private JLabel label;
        private int currentArticleIndex;

        public NewsGui(NewsFetcher newsFetcher) {
            super("News");

            // Set up the label
            label = new JLabel();
            currentArticleIndex = 0;

            // Set up the buttons
            JButton prevButton = new JButton("<");
            prevButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentArticleIndex > 0) {
                        currentArticleIndex--;
                        updateNews(newsFetcher);
                    }
                }
            });
            JButton nextButton = new JButton(">");
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentArticleIndex < newsFetcher.getNewsArticles().size() - 1) {
                        currentArticleIndex++;
                        updateNews(newsFetcher);
                    }
                }
            });

            // Set up the button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(prevButton);
            buttonPanel.add(nextButton);

            // Add the label and button panel to the frame
            add(label, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            // Set up the timer to update the news every 5 seconds
            Timer timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateNews(newsFetcher);
                }
            });
            timer.start();

            // Set the frame properties
            setSize(1000, 60);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private void updateNews(NewsFetcher newsFetcher) {
            ArrayList<String> articles = newsFetcher.getNewsArticles();
            if (!articles.isEmpty()) {
                String article = articles.get(currentArticleIndex);
                label.setText(article);
            }
        }
    }

    public static class NewsFetcher {
        private String city;
        private String startDate;
        private String endDate;
        private String sortOrder;
        private ArrayList<String> newsArticles;

//        public NewsFetcher(String city) {
//            this.city = city;
//            this.newsArticles = new ArrayList<>();
//            updateNews();
//        }
        public NewsFetcher(String city, String startDate, String endDate, String sortOrder) {
            this.city = city;
            this.startDate = startDate;
            this.endDate = endDate;
            this.sortOrder = sortOrder;
            this.newsArticles = new ArrayList<>();
            updateNews();
        }

        public ArrayList<String> getNewsArticles() {
            return newsArticles;
        }

        public void updateNews() {
            // Set up the API endpoint and parameters
            String apiKey = "f84a076db60248bf995cba3c7e171536";
//            String urlString = "https://newsapi.org/v2/everything?q="+city+"&from=2023-08-04&to=2023-08-04&sortBy=popularity&apiKey="+apiKey; 
            String urlString = "https://newsapi.org/v2/everything?q="+city+"&from="+startDate+"&to="+endDate+"&sortBy="+sortOrder+"&apiKey="+apiKey;
            try {
                // Create a URL object from the endpoint string
                URL url = new URL(urlString);

                // Open a connection to the URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                conn.setRequestMethod("GET");

                // Read the response from the API
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray articles = jsonResponse.getJSONArray("articles");

                // Update the news articles
                newsArticles.clear();
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.getJSONObject(i);
                    String title = article.getString("title");
                    newsArticles.add(title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

