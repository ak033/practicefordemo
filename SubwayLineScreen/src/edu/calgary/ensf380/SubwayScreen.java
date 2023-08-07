/* File: SubwayScreen.java
 * Subway screen GUI that takes in all info and makes a GUI screen with components
 */
package edu.calgary.ensf380;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SubwayScreen extends JFrame{
	private JFrame mainFrame;
	private JPanel adScreen;
	private JPanel weatherScreen;
	private JPanel newsScreen;
	private JPanel trainScreen;
    private String newsText;
    private JLabel newsLabel = new JLabel(newsText);
    
	public SubwayScreen() {
		mainFrame = new JFrame(" Welcome to The Subway ");
		mainFrame.setSize(1000,700); //(w, l)
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		
		// Create four panels for the sections
		adScreen = new JPanel();
		weatherScreen = new JPanel();
		newsScreen = new JPanel();
		trainScreen = new JPanel();
		
		//Get Advertisement Pictures
        ImageIcon adIcon = new ImageIcon("C:\\Users\\User\\OneDrive\\Pictures\\Screenshots\\Screenshot_20221107_062635.png\"); 
        Image resizedAd = adIcon.getImage().getScaledInstance(633, 400, Image.SCALE_SMOOTH);
        ImageIcon resizedAdIcon = new ImageIcon(resizedAd);
        JLabel adLabel = new JLabel();
        adLabel.setIcon(resizedAdIcon);
        
        //get Weather Picture
        ImageIcon weatherIcon = new ImageIcon("C:\\Users\\User\\OneDrive\\Pictures\\Screenshots\\Screenshot 2023-08-05 211412.png"); 
        Image resizedWeather = weatherIcon.getImage().getScaledInstance(633, 400, Image.SCALE_SMOOTH);
        ImageIcon resizedWeatherIcon = new ImageIcon(resizedWeather);
        JLabel weatherLabel = new JLabel();
        weatherLabel.setIcon(resizedWeatherIcon);
		
        //get trainInfo Picture
        ImageIcon trainIcon = new ImageIcon("C:\\Users\\User\\eclipse-workspace\\WeatherCity\\bin\\WEATHER\\weatherIcon.png"); 
        Image resizedTrain = trainIcon.getImage().getScaledInstance(1000, 190, Image.SCALE_SMOOTH);
        ImageIcon resizedTrainIcon = new ImageIcon(resizedTrain);
        JLabel trainLabel = new JLabel();
        trainLabel.setIcon(resizedTrainIcon);
        
        //News Scroll
        newsText = "Breaking News: The Antichrist has been spotted in Manhatten! ";
        newsLabel.setFont(new Font("Arial", Font.BOLD, 30));
        
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newText = newsText.substring(1) + newsText.charAt(0);
                newsText = newText;
                newsLabel.setText(newText);
            }
        });
        timer.start();
        
        
        //Size all 4 screens and add to screens
		adScreen.setPreferredSize(new Dimension(633, 400));
		adScreen.add(adLabel);
		
		weatherScreen.setPreferredSize(new Dimension(367, 400));
		weatherScreen.add(weatherLabel);
		
		newsScreen.setPreferredSize(new Dimension(1000, 50));
		newsScreen.add(newsLabel);
		
		trainScreen.setPreferredSize(new Dimension(1000, 250));
		trainScreen.add(trainLabel);
		
		// Split the screen into four sections using JSplitPane
		JSplitPane topSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, adScreen, weatherScreen);
		topSplit.setDividerLocation(633);
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, newsScreen, trainScreen);
		bottomSplit.setDividerLocation(50);
		
		JSplitPane middleSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplit, bottomSplit);
		middleSplit.setDividerLocation(400);
		
		//add screens to mainScreen
		mainFrame.getContentPane().add(middleSplit);
		
		// Adjust the frame size and make it visible
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new SubwayScreen();
		});
	}  
}