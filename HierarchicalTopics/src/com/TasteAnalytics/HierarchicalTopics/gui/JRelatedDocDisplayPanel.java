/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.HierarchicalTopics.gui;

import com.TasteAnalytics.HierarchicalTopics.datahandler.LDAHTTPClient;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Lee
 */
public class JRelatedDocDisplayPanel extends JPanel{
    
    JLabel titleLabel = new JLabel();
    JScrollPane termsScrollPane;
    JScrollPane abstractScrollPane;
    	StyledDocument doc;
        String _id;
    public JRelatedDocDisplayPanel(
            HashSet<String> tokens, HashSet<String> interTokens, String title, String Abstract, String id, final String host, final String collection,
    final HashSet<String> hide)
    {
        _id = id;
        this.setPreferredSize(new Dimension(300, 400));
        this.setLayout(new BorderLayout(0,5));
        titleLabel.setText(title);
        titleLabel.setToolTipText(title);
        
        
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount()==2)
                {
                    List<String> _ids = new ArrayList<String>();
                    _ids.add(_id);
                    try {
                        LDAHTTPClient c = new LDAHTTPClient("https", host, "2012");
                        c.login(true,null,null);
                        BasicDBList x = (BasicDBList)c.getTweetsEntity( collection,_ids);
                        
                        BasicDBObject dbo = (BasicDBObject) x.get(0);
                        
                        JFrame jf = new JFrame();
                        jf.setTitle("Single Document View");
                        jf.setSize(500,800);
                        jf.setVisible(true);
                        JTextArea jta = new JTextArea();
                        JScrollPane  jsp = new JScrollPane(jta);
                        jf.add(jsp);
                        
                         ImageIcon logo_icon = new ImageIcon ( 
                        Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().
			getResource("resource/logo.png")));
                      jf.setIconImage(logo_icon.getImage());
                        
                        HashMap<?,?> x1 = (HashMap<?,?>)dbo;
                        
                        for (Object o : dbo.keySet())
                        {
                            if (!hide.contains(String.valueOf(o)))
                            {
                                
                                jta.append(String.valueOf(o) + ":");
                                jta.append("\n");
                                 jta.append(String.valueOf(dbo.get(o)));
                                jta.append("\n");
                            }
                        }
                        jta.setLineWrap(true);
                        
                        jta.setCaretPosition(0);

                    } catch (IOException ex) {
                        Logger.getLogger(JRelatedDocDisplayPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
       
                }
                
                
            }
});
        
        JTextArea abstractText = new JTextArea(Abstract);
        abstractText.setEditable(false);
        abstractScrollPane = new JScrollPane(abstractText);
        abstractText.setLineWrap(true);
        abstractText.setCaretPosition(0);
        
        
        JTextPane termsPane = new JTextPane();
        termsPane.setEditable(false);
        doc = termsPane.getStyledDocument();
	addStylesToDocument(doc);
        
        
        for (String s : tokens)
        {
            
          

            try {					
                            if (interTokens.contains(s))
                            {

                                    doc.insertString(doc.getLength(), s + " ", doc.getStyle("HIGHLIGHT"));
                            }
                            else
                            {

                                    doc.insertString(doc.getLength(), s + " ", doc.getStyle("TEXT"));
                            }
                    } catch (BadLocationException e) {						
                            e.printStackTrace();
                    }
			
			
            
            
        }
        
        termsPane.setCaretPosition(0);
        
        
        termsScrollPane = new JScrollPane(termsPane);
        
        termsScrollPane.setPreferredSize(new Dimension(this.getWidth(), 180));
        abstractScrollPane.setPreferredSize(new Dimension(this.getWidth(), 200));
        this.add(titleLabel, BorderLayout.PAGE_START);
        this.add(termsScrollPane, BorderLayout.CENTER);
        this.add(abstractScrollPane, BorderLayout.PAGE_END);
        
    }
    
    	
protected void addStylesToDocument(StyledDocument doc) {
		
		
		Style def = StyleContext.getDefaultStyleContext().
			getStyle(StyleContext.DEFAULT_STYLE);
				
			
		Style s = doc.addStyle("HIGHLIGHT", def);
		StyleConstants.setBold(s, true);
		
		StyleConstants.setForeground(s, new Color(85, 172,238));
		
		
		

		Style sc = doc.addStyle("TEXT", def);		
//		StyleConstants.setFontSize(sc, ViewPreferences.twitterFont.getSize());
//		StyleConstants.setFontFamily(sc, ViewPreferences.twitterFont.getFamily());
		StyleConstants.setForeground(sc, new Color(41, 47, 51));
//		MutableAttributeSet attrs = jta.getInputAttributes();
//		
//		StyleConstants.setForeground(attrs, ColorUtils.twitterBlueColor);
		
		
		
	}
}
