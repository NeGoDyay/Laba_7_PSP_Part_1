package com.bsuir.nikitayasiulevich;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MovieLibraryApplication extends JFrame {
    private JLabel titleLabel;
    private JTable moviesTable;
    private DefaultTableModel tableModel;

    public MovieLibraryApplication() {
        super("Movie Library");

        // Create and set up the main window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the title label
        titleLabel = new JLabel("Movie Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Create the table model and table
        tableModel = new DefaultTableModel();
        moviesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(moviesTable);
        add(scrollPane, BorderLayout.CENTER);
        moviesTable.setAutoCreateRowSorter(true);

        // Create the buttons
        JButton addButton = new JButton("Add Movie");
        JButton deleteButton = new JButton("Delete Movie");
        JButton editButton = new JButton("Edit Movie");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        add(buttonPanel, BorderLayout.SOUTH);


        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMovie();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMovie();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMovie();
            }
        });

        // Set up the database connection and populate the table
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_library", "root", "15101210201Ny@");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies");

            // Get the column names
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            // Get the data rows
            while (rs.next()) {
                Object[] rowData = new Object[numColumns];
                for (int i = 1; i <= numColumns; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        pack();
        setVisible(true);
    }

    private void sortMoviesByDirector() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(2, (o1, o2) -> {
            String director1 = (String) o1;
            String director2 = (String) o2;
            return director1.compareToIgnoreCase(director2);
        });
        moviesTable.setRowSorter(sorter);
    }

    private void sortMoviesByTitle() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(1, (o1, o2) -> {
            String title1 = (String) o1;
            String title2 = (String) o2;
            return title1.compareToIgnoreCase(title2);
        });
        moviesTable.setRowSorter(sorter);
    }

    private void addMovie() {
        // Prompt the user for movie details
        String title = JOptionPane.showInputDialog(this, "Enter title:");
        String director = JOptionPane.showInputDialog(this, "Enter director:");
        String releaseYearString = JOptionPane.showInputDialog(this, "Enter release year:");
        int releaseYear = Integer.parseInt(releaseYearString);

        // Add the movie to the table model
        Object[] rowData = {null, title, director, releaseYear};
        tableModel.addRow(rowData);

        // Insert the movie into the database
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_library", "root", "15101210201Ny@");
            String insertQuery = "INSERT INTO movies (title, director, release_year) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, title);
            stmt.setString(2, director);
            stmt.setInt(3, releaseYear);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow != -1) {
            int movieId = (int) moviesTable.getValueAt(selectedRow, 0);
            tableModel.removeRow(selectedRow);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_library", "root", "15101210201Ny@");
                String deleteQuery = "DELETE FROM movies WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(deleteQuery);
                stmt.setInt(1, movieId);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void editMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the movie ID from the selected row
            int movieId = (int) moviesTable.getValueAt(selectedRow, 0);

            // Prompt the user for updated movie details
            String title = JOptionPane.showInputDialog(this, "Enter updated title:");
            String director = JOptionPane.showInputDialog(this, "Enter updated director:");
            String releaseYearString = JOptionPane.showInputDialog(this, "Enter updated release year:");
            int releaseYear = Integer.parseInt(releaseYearString);

            // Update the movie in the table model
            moviesTable.setValueAt(title, selectedRow, 1);
            moviesTable.setValueAt(director, selectedRow, 2);
            moviesTable.setValueAt(releaseYear, selectedRow, 3);

            // Update the movie in the database
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/movie_library", "root", "15101210201Ny@");
                String updateQuery = "UPDATE movies SET title = ?, director = ?, release_year = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(updateQuery);
                stmt.setString(1, title);
                stmt.setString(2, director);
                stmt.setInt(3, releaseYear);
                stmt.setInt(4, movieId);
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MovieLibraryApplication();
            }
        });
    }
}