/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.gui;

/**
 *
 * @author Li
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Topics.java
 *
 * Created on Apr 28, 2010, 10:41:52 AM
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author wdou
 */
public class Topics   {

    private boolean hasNext = true;
    /** The ParallelDisplay component we are assigned to. */
    //ParallelDisplay parent;
    ViewController parent;
    /** To host topics and individual words - Each panel represent a topic
    and each button represents a word*/
    private JPanel[] panels;
    private JLabel[][] labels;
    private LayoutManager layout;
    

    /** Creates new form Topics */
    public Topics() {
       
    }



    public Topics(ViewController viewcontroller) {
        this();
        this.parent = viewcontroller;
           
    }

    private String topicUrl;
private List<String[]> allTopics;
    private List<String[]> reorganizedTopics;
    private List<int[]> occurances;
    private List<List<Dimension>> otherOccurances;
    private List<Float[]> colorMap;
    private List<Color> panelBckColors;

    public List<String[]> getReorganizedTopics(){
        return reorganizedTopics;
    }
    
    
    
    
    public void loadTopic(List<String[]> topics) throws IOException {

        allTopics = topics;
        //extractFrequency();
       


    }
    /**
     * Read the content of the file
     */
    private BufferedReader br;

    public void readContents() throws IOException {
        URLConnection conn = null;
        try {
            conn = new URL(topicUrl).openConnection();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Topics.class.getName()).log(Level.SEVERE, null, ex);
        }
        conn.connect();

        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        br = new BufferedReader(in);
        readAll();

    }

    private void readAll() throws IOException {
        allTopics = new ArrayList<String[]>();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) {
                allTopics.add(nextLineAsTokens);
            }

        }
        System.out.println(allTopics.size());
    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     *         entry.
     *
     * @throws IOException
     *             if bad things happen during the read
     */
    public String[] readNext() throws IOException {

        String[] result = null;

        String nextLine = getNextLine();
        if (!hasNext) {
            return result; // should throw if still pending?
        }
        String[] r = nextLine.split(",");

        result = r;

        return result;
    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws IOException
     *             if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Count the word frequency and store locations of other occurences
     */
    private void extractFrequency() {
        //Re-organize topics based on the similarities
        reorganizedTopics = new ArrayList<String[]>();
        reorganizedTopics.add(allTopics.get(0));
        for(int i=1; i<allTopics.size(); i++){
            int t = (i-1)+1;
            reorganizedTopics.add(allTopics.get(t));
        }

        if (reorganizedTopics != null) {
            occurances = new ArrayList<int[]>(reorganizedTopics.size());
            for (int i = 0; i < reorganizedTopics.size(); i++) {
                int[] temp = new int[reorganizedTopics.get(0).length];
                occurances.add(temp);
            }
            otherOccurances = new ArrayList<List<Dimension>>();
            int count = 1;
            Dimension keyPos, tempPos;
            List<Dimension> tmpDim = null;
            for (int i = 1; i < reorganizedTopics.size(); i++) {
                for (int j = 2; j < reorganizedTopics.get(0).length; j++) {
                    //Compare every word with other words
                    keyPos = new Dimension(i, j);
                    for (int m = 1; m < reorganizedTopics.size(); m++) {
                        for (int n = 2; n < reorganizedTopics.get(0).length; n++) {
                            if(m==i && n==j){
                                //Skip the word itself
                            }
                            else {
                                if (reorganizedTopics.get(i)[j].trim().equalsIgnoreCase(reorganizedTopics.get(m)[n].trim())) {
                                    count++;
                                    tempPos = new Dimension(m, n);
                                    if (count == 2) {//no dimension array has been created for the current word
                                        tmpDim = new ArrayList<Dimension>();
                                        tmpDim.add(keyPos);
                                    }
                                    tmpDim.add(tempPos);
                                }
                            }
                        }
                    }
                    if (tmpDim != null) {
                        otherOccurances.add(tmpDim);
                    }
                    if (tmpDim != null) {
                        for (int q = 0; q < tmpDim.size(); q++) {
                            occurances.get(tmpDim.get(q).width)[tmpDim.get(q).height] = count;
                        }
                    }
                    tmpDim = null;
                    count = 1;
                }
            }
        }
        System.out.println("Words that show up more than twice: "+occurances.size());
    }



    private void displayTopics() {
        int numOfTopics = 0, numOfWords = 0;

        if (allTopics != null) {
            numOfTopics = allTopics.size();
            numOfWords = allTopics.get(0).length;
            panels = new JPanel[numOfTopics];
            labels = new JLabel[numOfTopics][numOfWords];
        }
        
        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0,0,0,0);
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      
        for (int i = 0; i < numOfTopics; i++) {
            panels[i] = new JPanel();
            //gbag.setConstraints(panels[i], gbc);
            panels[i].setLayout(new FlowLayout(FlowLayout.LEFT));
            panels[i].setPreferredSize(new Dimension(428, 40));

            for (int j = 1; j < numOfWords; j++) {//Skip the first word every time
                labels[i][j] = new JLabel(allTopics.get(i)[j]);
                Font f = new Font("Font", Font.PLAIN, 3*occurances.get(i)[j]+12);
                //Font f = new Font("Font", Font.PLAIN, 3 * occurances.get(i)[j] + 32);//demo
                labels[i][j].setFont(f);
                labels[i][j].setName(Integer.toString(i) +"," + Integer.toString(j));
              
                panels[i].add(labels[i][j]);
            }
           
        }

        
       
        
    }

    private void displayTopics(List<Integer> sequence) {
        if(sequence != null){

        int numOfTopics = 0, numOfWords = 0;

        if (allTopics != null) {
            numOfTopics = allTopics.size();//first line is the header
            numOfWords = allTopics.get(0).length;
            panels = new JPanel[numOfTopics];//first line is the header
            labels = new JLabel[numOfTopics][numOfWords];
        }

        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0,0,0,0);
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      


        int tmpi = 0;
        panels[tmpi] = new JPanel();
            //gbag.setConstraints(panels[i], gbc);
            panels[tmpi].setLayout(new FlowLayout(FlowLayout.LEFT));
            //panels[tmpi].setPreferredSize(new Dimension(428, 32));
            panels[tmpi].setPreferredSize(new Dimension(856, 62));//for demo
            for (int k = 1; k < numOfWords; k++) {
                labels[0][k] = new JLabel(allTopics.get(0)[k]);
                //Font ff = new Font("Font", Font.PLAIN, 3 * occurances.get(0)[k] + 12);
                Font ff = new Font("Font", Font.PLAIN, 5 * occurances.get(0)[k] + 32);//for demo
                labels[0][k].setFont(ff);
                labels[0][k].setName(Integer.toString(tmpi) + "," + Integer.toString(k));
                
                panels[0].add(labels[0][k]);
            }
           

           setColorMap();
           panelBckColors = new ArrayList<Color>();
            for (int i = 1; i < numOfTopics; i++) {//first line is the header
                //int tmpid = sequence.get(i-1)+1;

                panels[i] = new JPanel();
                panels[i].setLayout(new FlowLayout(FlowLayout.LEFT));
                //panels[i].setPreferredSize(new Dimension(428, 32));
                panels[tmpi].setPreferredSize(new Dimension(856, 62));//for demo
                /**
                 * sequence is 1 less in size than numOfTopics since numOfTopics contains the header
                 */
                for (int j = 1; j < numOfWords; j++) {//Skip the first word every time
                    labels[i][j] = new JLabel(reorganizedTopics.get(i)[j]);//Based on re-ordered topics
                    //Font f = new Font("Font", Font.PLAIN, 2 * occurances.get(i)[j] + 12);
                    Font f = new Font("Font", Font.PLAIN, 5 * occurances.get(i)[j] + 32);//For demo
                    labels[i][j].setFont(f);
                    labels[i][j].setName(Integer.toString(i) + "," + Integer.toString(j));
                  
                    panels[i].add(labels[i][j]);
                }
                try{
                Color tmpColor = Color.getHSBColor(colorMap.get(i-1)[1], (float) (colorMap.get(i - 1)[2]-0.4), colorMap.get(i-1)[3]);
                //Color tmpColor = new Color(colorMap.get(i-1)[1], colorMap.get(i - 1)[2], colorMap.get(i-1)[3], (float) 0.3);
                panels[i].setBackground(tmpColor);
                panelBckColors.add(tmpColor);
                }catch(Exception e){
                    System.out.println("Can't assign colors to topic panels");
                }
             

            }

           
           
        }

    }
    // Variables declaration - do not modify                     
    // End of variables declaration                   


    private int currentWord = -1;//for unhighlighting when mouse exited



    private void highlightOtherOccurance(String[] tmpString) {
        
        
        Dimension tmpD = null;
        if (tmpString != null) {
            tmpD = new Dimension(Integer.parseInt(tmpString[0].trim()),
                    Integer.parseInt(tmpString[1].trim()));
        }
        for (int i = 1; i < otherOccurances.size(); i++) {
            for (int j = 0; j < otherOccurances.get(i).size(); j++) {

                if ((tmpD.width == otherOccurances.get(i).get(j).width) && (tmpD.height == otherOccurances.get(i).get(j).height)) {
                    currentWord = i;
                    for (int k = 0; k < otherOccurances.get(i).size(); k++) {
                        if (k != j) {
                            labels[otherOccurances.get(i).get(k).width][otherOccurances.get(i).get(k).height].setForeground(Color.blue);
                        }
                    }
                }
            }
        }
    }
    
    int[] previousHighlightedKW = null;
    int previousTopic = -1;
    
    public void highlightKeywords(int topic, int[] index){
        unHighlight(previousTopic, previousHighlightedKW);
        for(int i=0; i<index.length; i++){
            labels[topic+1][index[i]+2].setForeground(Color.blue);
            
            //test
            System.out.print(labels[topic+1][index[i]+2].getText() + " ");
        }
        System.out.println();
        previousHighlightedKW = index;
        previousTopic = topic;
    }

    private void unHighlight() {
        if(currentWord != -1){
            for(int i=0; i<otherOccurances.get(currentWord).size(); i++){
                labels[otherOccurances.get(currentWord).get(i).width][otherOccurances.get(currentWord).get(i).height].setForeground(Color.DARK_GRAY);
            }
        }
        currentWord = -1;
    }

    private void unHighlight(int t, int[] k){
        if(previousHighlightedKW != null){
            for(int i=0; i<previousHighlightedKW.length; i++){
                labels[t + 1][previousHighlightedKW[i] + 2].setForeground(Color.black);
            }
        }
    }
    
    public void setColorMap(){
        colorMap = new ArrayList<Float[]>();
        try{
            colorMap = this.parent.getNumericalColors();
        }catch(Exception ex){
            System.out.println("TopicDisplay getColor failed");
        }
    }


    public void topicHighlighted(int t) {
        if(colorMap == null){
            setColorMap();
        }
        Color tmpColor = Color.getHSBColor(colorMap.get(t-1)[1], colorMap.get(t-1)[2], colorMap.get(t-1)[3]);
        //Color tmpColor = new Color(colorMap.get(t-1)[1], colorMap.get(t-1)[2], colorMap.get(t-1)[3]);
        panels[t].setBackground(tmpColor);
        
  
    }

    public void topicDeHilighted() {
        panels[0].setBackground(new Color(240,240,240));
        for(int i=1; i<panels.length; i++){
            panels[i].setBackground(panelBckColors.get(i-1));
        }
        
    }

 
}

