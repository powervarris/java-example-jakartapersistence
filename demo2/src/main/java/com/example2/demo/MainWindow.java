package com.example2.demo;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.json.*;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textEmail;
	private DefaultListModel<String> userListModel;
	private JList<String> userList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(113, 93, 190, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Name:");
		lblNewLabel.setBounds(113, 73, 115, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(113, 134, 115, 14);
		contentPane.add(lblEmail);
		
		textEmail = new JTextField();
		textEmail.setColumns(10);
		textEmail.setBounds(113, 150, 190, 20);
		contentPane.add(textEmail);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = textField.getText();
				String email = textEmail.getText();
				try {
					
					URL url = new URL("http://localhost:8080/api/users");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setDoOutput(true);
					
					String jsonInputString = String.format("{\"name\": \"%s\", \"email\": \"%s\"}", name, email);
					
					try (java.io.OutputStream os = conn.getOutputStream()) {
						byte[] input = jsonInputString.getBytes("utf-8");
						os.write(input, 0, input.length);
					}
					
					int responseCode = conn.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
						JOptionPane.showMessageDialog(contentPane, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
						textField.setText("");
						textEmail.setText("");
						loadUsers();
					} else {
						System.out.println("Failed to create user. Response code: " + responseCode);
					}
				}catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(contentPane, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSubmit.setBounds(165, 204, 89, 23);
		contentPane.add(btnSubmit);
		
		userListModel = new DefaultListModel<>();
		userList = new JList<>(userListModel);
		JScrollPane scrollPane = new JScrollPane(userList);
		scrollPane.setBounds(320, 30, 150, 200);
		contentPane.add(scrollPane);
		
		JLabel lblUsers = new JLabel("Users:");
		lblUsers.setBounds(320, 10, 100, 14);
		contentPane.add(lblUsers);
		
		loadUsers();
		
	}
	
	private void loadUsers() {
		SwingUtilities.invokeLater(() -> {
			userListModel.clear();
			try {
				URL url = new URL("http://localhost:8080/api/users");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				
				if (conn.getResponseCode() == 200) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line.trim());
                        }
                        JSONArray arr = new JSONArray(response.toString());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String user = obj.getString("name") + " (" + obj.getString("email") + ")";
                            userListModel.addElement(user);
                        }
                    }
                }
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(contentPane, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
