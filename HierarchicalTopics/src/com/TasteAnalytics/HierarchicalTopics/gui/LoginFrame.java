/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.HierarchicalTopics.gui;

/**
 *
 * @author Lee
 */
/**
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;




/**
 * Copyright 2012, 2014, Taste Analytics, LLC and/or its affiliates. All rights reserved.
 * @author Lee
 * @version 1000
 */
public class LoginFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1517340244782572840L;

	private String username;
	private String password;
	private JPasswordField passwordField;
	private JButton jButtonLogin = new JButton();
	private JLabel jLabelUserName = new JLabel();
	private JLabel jLabelPassword = new JLabel();
	private JLabel jLabelServer = new JLabel();
	private JTextField jTextFieldUserName = new JTextField();
	private JComboBox<Object> serverSelector = new JComboBox<Object>();
	
	private JLabel taLogoLabel = new JLabel();
	ViewController vc;
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LoginFrame(ViewController vc) {
		this.vc = vc;
	this.setVisible(true);
		//;//;//"192.168.0.17";//
		
		// "10.18.202.126";//\
		this.setLayout(new BorderLayout());
		JPanel displayPanel = new JPanel();
		
		//serverSelector.addItem((String)"192.168.0.17");
		serverSelector.addItem((String)"caprica.uncc.edu");
		//serverSelector.addItem((String)"54.209.61.133");
		
		
		//serverSelector.addItem((String)"");
		//serverSelector.addItem((String)"");
		//serverSelector.addItem((String)"");

		//displayPanel.setBackground(ColorUtils.mainColor);
		
		displayPanel.setLayout(null);
		
		displayPanel.setPreferredSize(new Dimension(500, 500));
		this.setSize(new Dimension(500, 600));
		this.setLocation(
				Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 250,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 250);

		passwordField = new JPasswordField(10);
		
		displayPanel.add(jLabelServer);
		
		displayPanel.add(serverSelector);
		
		displayPanel.add(jLabelUserName);
		displayPanel.add(jLabelPassword);
		displayPanel.add(jButtonLogin);
		displayPanel.add(jTextFieldUserName);
		displayPanel.add(passwordField);
		
		jLabelServer.setBounds(new Rectangle(75, 75, 100, 25));
		jLabelServer.setText("Server");
		serverSelector.setBounds(new Rectangle(250, 75, 200, 25));

		
		
		jLabelUserName.setBounds(new Rectangle(75, 150, 100, 25));
		jLabelUserName.setText("User Name");
		jLabelUserName.setForeground(Color.DARK_GRAY);
		//jLabelUserName.setBackground(ColorUtils.mainColor);
		
		jLabelPassword.setBounds(new Rectangle(75, 200, 100, 25));
		jLabelPassword.setText("Password");
		jLabelPassword.setForeground(Color.DARK_GRAY);

		jButtonLogin.setText("Login");
		jButtonLogin.setBounds(new Rectangle(200, 300, 100, 50));

		jTextFieldUserName.setBounds(new Rectangle(250, 150, 200, 25));
		
		passwordField.setBounds(new Rectangle(250, 200, 200, 25));


		
		
		
		
		
		jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					jButtonLoginActionPerformed(evt);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
		this.add(displayPanel,BorderLayout.CENTER);
		
		this.getRootPane().setDefaultButton(jButtonLogin);
		jButtonLogin.requestFocus();

	}

	private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) throws IOException {

		username = jTextFieldUserName.getText();

		char[] input = passwordField.getPassword();

		password = String.valueOf(input);

		
		
		String servername = (String)serverSelector.getSelectedItem();
		
		vc.host = servername;
		
		boolean connectionSuccess = vc.InitializeNetworkConnection(
				false, username, password);

		if (connectionSuccess) {
			
			File loginKeyFile = new File("./...key");
			
			BufferedReader br = new BufferedReader(new FileReader(
					loginKeyFile));

			NetworkMetaInformation.CookieString = br.readLine();
			br.close();
			
			vc.getParentFrame().setVisible(true);
						
			setVisible(false);
			dispose(); // Destroy the JFrame object
		} else {
			// dialog wrong
			JOptionPane
					.showMessageDialog(
							this,
							"Wrong Username or Password.\nPlease contact Taste Analytics for more information. ",
							"Login Failed.", JOptionPane.ERROR_MESSAGE);
		}
	}

}
