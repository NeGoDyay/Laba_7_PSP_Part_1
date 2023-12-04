package com.bsuir.nikitayasiulevich;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "movie_library";
        String driver = "com.mysql.cj.jdbc.Driver";
        String userName = "root";
        String password = "password";

        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stmt = conn.createStatement();

            // Create the database
            String createDatabaseQuery = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(createDatabaseQuery);

            // Use the database
            String useDatabaseQuery = "USE " + dbName;
            stmt.executeUpdate(useDatabaseQuery);

            // Create the movies table
            String createTableQuery = "CREATE TABLE movies (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "title VARCHAR(255) NOT NULL," +
                    "director VARCHAR(255) NOT NULL," +
                    "release_year INT" +
                    ")";
            stmt.executeUpdate(createTableQuery);

            stmt.close();
            conn.close();
            System.out.println("Database and table created successfully.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
