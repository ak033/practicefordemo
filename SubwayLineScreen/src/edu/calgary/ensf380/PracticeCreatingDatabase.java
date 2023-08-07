package edu.calgary.ensf380;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

//public static String getCSVName(String path);
//makeSQLTableFromFile(Connection conn, String csvFilePath, String tableName);
//addData(Connection conn, String csvFilePath, String tableName);
//clearTable(Connection conn, String tableName);

public class PracticeCreatingDatabase {
	public static final String url = "jdbc:mysql://localhost:3306/practicetraincsv";
	public static final String username = "BigMac";
	public static final String password = "1234";
	public String filepath;
	
    public static void main(String[] args) throws IOException {
    	String csvFilePath = "C:\\Users\\Uruba\\Documents\\380 Final\\SubwayLineScreen\\src\\Simulator\\out\\Trains_1680832574555.csv";
        String databaseName = "PracticeTrainCSV";    
        
        try {
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement stmt = conn.createStatement(); 
			String createDatabaseQuery = "USE " + databaseName;
			stmt.executeUpdate(createDatabaseQuery);
			
			//Table 1: 
			String tableName = getCSVName(csvFilePath);
	        System.out.println(tableName);
	        
	        //makeSQLTableFromFile(conn, csvFilePath, tableName);
	        //addData(conn, csvFilePath, tableName);
	        //clearTable(conn, tableName);
	        
	        
	        
	        
	     
        } catch (SQLException e) {
            e.printStackTrace(); System.out.println("Database creation problem!");
         }
    }//main end
    
    public static String getCSVName(String path) {	
    //gives name of csv using filepath
    	Pattern pattern = Pattern.compile("[^\\\\/]+\\.csv$");
        
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            String csvFileName = matcher.group().replace(".csv", "");
            return csvFileName;
        }else {
        	return null;
        }
    }
    public static void clearTable(Connection conn, String tableName) throws SQLException {
    		
    		Statement statement = conn.createStatement();
            String delete = "DELETE FROM " + tableName;
            statement.executeUpdate(delete);

            System.out.println("all records deleted");

        
    }
    
    public static void makeSQLTableFromFile(Connection conn, String csvFilePath, String tableName) throws IOException {
    //makes sql table from file given filepath
				//start creating table in MySQL
				BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
                String firstLine = reader.readLine();
                
                if (firstLine != null) {
                    String[] columns = firstLine.split(",");
                    StringBuilder createTable = new StringBuilder("CREATE TABLE " + tableName + "(" );
                    
                    for(String col: columns) {
                    	System.out.println(col);	//print out each col to see if correct
            			createTable.append(col).append(" VARCHAR(200), "); //each length is 200 
            		}
                    createTable.setLength(createTable.length() - 2); // Remove the last comma by shortening length by 2 
                    createTable.append(")");
                    
                    //create table
                    try (PreparedStatement createTableStmt = conn.prepareStatement(createTable.toString())) {
                        createTableStmt.execute();
                        System.out.println("Table created successfully");
                    }catch(SQLException e) {
                    	e.printStackTrace();
                    	System.out.println("Creation problem!");
                    }
                    
                }else {
                	System.out.println("File is Empty");
                }
                reader.close();                
    }//end makeTable()
    
    public static void addData(Connection conn, String csvFilePath, String tableName) throws SQLException, IOException {
    	
               BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
               ArrayList<String> cols = getColumns(tableName, conn);
               
               String line;
               reader.readLine(); //Skip header
               while ((line = reader.readLine()) != null) {
                   String[] values = line.split(",");
                       if(values.length == cols.size()) {	                                     
	                   String insertQuery = "INSERT INTO " + tableName + "("+ 
	                		   String.join(", ", cols) + ") VALUES (" +
	                		   String.join(", ", getQM(cols.size()))+ ")";
	                   
	                  	                		   
	                   try(PreparedStatement preparedStatement = conn.prepareStatement(insertQuery)){
	                	   for(int i = 0; i < values.length; i++) {
	                		   preparedStatement.setString(i+1, values[i]);
	                	   }
	                       preparedStatement.executeUpdate();
	                   }
                   }else {
                	   System.out.println("values != column");
                   }
               
               
               System.out.println("Data inserted successfully.");
               }
               reader.close();
    }
               
    public static ArrayList<String> getQM(int num){	//used by addData
    	ArrayList<String> qm = new ArrayList<>();
    	for(int i = 0; i < num; i++) {
    		qm.add("?");
    	}
    	return qm;
    }
    
    public static ArrayList<String> getColumns(String tableName, Connection conn) throws SQLException {//used by addData
    			
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
               ResultSetMetaData metaData = resultSet.getMetaData();
               int columnCount = metaData.getColumnCount();
               
               ArrayList<String> columnList = new ArrayList<String>();
           
               for (int i = 1; i <= columnCount; i++) {
                   columnList.add(metaData.getColumnName(i));
               }
               return columnList;
           
    }
    
}
