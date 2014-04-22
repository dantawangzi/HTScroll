/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author Li Yu
 */
public class LoginPanel extends JPanel{
    
    String username;
    String password;
    JPasswordField passwordField;
    
    ViewController parent;
    
    //JPanel contentPanel = new JPanel();
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
    
    
    private javax.swing.JButton jButtonLogin = new javax.swing.JButton();
    private javax.swing.JLabel jLabel1 = new  javax.swing.JLabel();
    private javax.swing.JLabel jLabel2 = new  javax.swing.JLabel();
    private javax.swing.JTextField jTextFieldUserName = new javax.swing.JTextField();
    
    
    LoginPanel(ViewController vc)
    {
        
        this.setBackground(new java.awt.Color(39, 39, 39));
        
        //this.add(contentPanel);
        passwordField = new JPasswordField(10);
        
        
        this.setPreferredSize(new Dimension(500,500));
        
        this.setLayout(null);
        
        this.add(jLabel1);
        this.add(jLabel2);
        this.add(jButtonLogin);
        this.add(jTextFieldUserName);
        this.add(passwordField);
        
       jLabel1.setBounds(new Rectangle(50,50,100,25));
       jLabel1.setText("User Name");
       jLabel1.setBackground(Color.white);
       jLabel2.setBounds(new Rectangle(50,150,100,25));
       jLabel2.setText("Password"); 
        jLabel2.setBackground(Color.white);
       
       jButtonLogin.setText("Login");
       jButtonLogin.setBounds(new Rectangle(250,250,100,50));
       
       
       jTextFieldUserName.setBounds(new Rectangle(250,50,100,25));
       passwordField.setBounds(new Rectangle(250,150,100,25));
       
        this.setSize(new Dimension(500,500));
        
        parent = vc;
        
        
        

        
        jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoginActionPerformed(evt);
            }
        });
        
        
        
        
        
    }
    
    private void jButtonLoginActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        
        username = jTextFieldUserName.getText();
        
        
        
        char[] input = passwordField.getPassword();
        
        password = String.valueOf(input);
        
  
//            if (isPasswordCorrect(input)) {
//                JOptionPane.showMessageDialog(controllingFrame,
//                    "Success! You typed the right password.");
//            } else {
//                JOptionPane.showMessageDialog(controllingFrame,
//                    "Invalid password. Try again.",
//                    "Error Message",
//                    JOptionPane.ERROR_MESSAGE);
//            }
// 
//            //Zero out the possible password, for security.
//            Arrays.fill(input, '0');
 
  
           // passwordField.selectAll();
           // resetFocus();
        
       
        parent.mainFrame.getmViewPanel().remove(this);
        
        
        parent.mainFrame.getmViewPanel().revalidate();
        parent.mainFrame.getmViewPanel().repaint();
        
        
        
        
        
        
        
    }                                            
    
    
//    private static boolean isPasswordCorrect(char[] input) {
//    boolean isCorrect = true;
//    char[] correctPassword = { 'b', 'u', 'g', 'a', 'b', 'o', 'o' };
//
//    if (input.length != correctPassword.length) {
//        isCorrect = false;
//    } else {
//        isCorrect = Arrays.equals (input, correctPassword);
//    }
//
//    //Zero out the password.
//    Arrays.fill(correctPassword,'0');
//
//    return isCorrect;
//}
    
    
    
    
    
}
