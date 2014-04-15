/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.Apollo.eventsview;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.TasteAnalytics.Apollo.TemporalView.CategoryStream;
import com.TasteAnalytics.Apollo.TemporalView.TimeColumn;

/**
 *
 * @author Wenwen
 */
public class EventsViewInteractions implements MouseListener, MouseMotionListener{
    
    private EventsViewPanel attachedPanel;
    private CategoryStream focusedStream;
    private TimeColumn focusedColumn;
    private boolean isClicked;
    private int[] currentKw = null;

    private myPopup popUp;
    
    
    
    public EventsViewInteractions(EventsViewPanel panel){
        attachedPanel = panel;
        isClicked = false;
        
         popUp = new myPopup();
    }
    
    private void clearPreviousFocuses() {
        if (focusedStream != null) {
            focusedStream = null;
        }

        if (focusedColumn != null) {
            focusedColumn = null;
        }

        attachedPanel.setFocusedCatgory(-99);
        attachedPanel.setFocusedColumn(-99);
    }
    
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        
        
         if (SwingUtilities.isRightMouseButton(e)) {

            mouseX = e.getX();
            mouseY = e.getY();

         
            popUp.show(e.getComponent(), e.getX(), e.getY());


            popUp.setCurrentPanelString(e.getComponent().getName());

        }
        
        clearPreviousFocuses();
        
        int currentYear = 0;
        for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
            TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
            if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                focusedColumn = timeColumn;
                focusedColumn.setIsFocused(true);
                attachedPanel.setFocusedColumn(i);//Hightlight year

                
                currentYear = i;
                attachedPanel.parent.fireYearSelected(attachedPanel.getData().idxOfDocumentPerSlot.get(i));

                if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        List<Integer> selectedByYearTopic = new ArrayList<Integer>();
                        // Check if there is a double click
                        if (e.getClickCount() == 2) {                           
                            
                           int size = 0, idx = 0;
                        for (int j = 0; j < attachedPanel.getCategoryAreas().size(); j++) {
                            if (attachedPanel.getCategoryAreas().get(j).getRenderRegion().contains(mouseX, mouseY)) {

                                //jth topic
                                size = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).size();
                                idx = 0;
                                for (int k = 0; k < size; k++) {
                                    idx = attachedPanel.getData().idxOfDocumentPerSlot.get(currentYear).get(k);
                                    //currentT = attachedPanel.getData().getTopicSequences().get(j);
                                    if (attachedPanel.getData().values_Norm.get(idx)[j] > 0.25) {
                                        selectedByYearTopic.add(idx);
                                    }
                                }

                                if (!selectedByYearTopic.isEmpty()) {
                                    attachedPanel.parent.fireYearTopicSelected(selectedByYearTopic);
                                }

                                selectedByYearTopic.clear();
                                break;
                            }
                            //break;

                        }

                            isClicked = true;
                        } else {
                            // Regardless of Double-Click or Single-Click, System should update the tagCloudView.
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        isClicked = false;
                        if (e.getClickCount() == 2) {
                    }

                    attachedPanel.repaintView();
                    break;
                }
            }
        }
    }
    }

    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (!isClicked) {
            clearPreviousFocuses();

            for (int i = 0; i < attachedPanel.getTimecolumns().size(); i++) {
                TimeColumn timeColumn = attachedPanel.getTimecolumns().get(i);
                if (timeColumn.getRenderRegion().contains(mouseX, mouseY)) {
                    focusedColumn = timeColumn;
                    //focusedColumn.setIsFocused(true);
                    //attachedPanel.setFocusedColumn(i);//Hightlight year
                   

                    //ActivityTypes at = null;
                    for (int j = 0; j < attachedPanel.getCategoryAreas().size(); j++) {
                        CategoryStream categoryStream = attachedPanel.getCategoryAreas().get(j);
                        if (categoryStream.getRenderRegion().contains(mouseX, mouseY)) {
                            focusedStream = categoryStream;
                            focusedStream.setIsHighlight(true);
                          
                            attachedPanel.setFocusedCatgory(j);
                            
                            attachedPanel.parent.topicChanged = true;
                           
                            
                            currentKw = attachedPanel.getData().topicYearKwIdx.get(j).get(i);
                          
                           

                            break;
                        }
                    }

                    break;
                }
            }
        }

        attachedPanel.repaintView();
    }

 


    
    
    
    
     class myPopup extends JPopupMenu implements ActionListener {
        //JPopupMenu removePanel;

      
        int targetRegion = 0;
        String currentPanelString;
         JSlider eventParaSlider;
        JLabel thresholdLabel;
        JCheckBoxMenuItem cbEventMenuItem;
        float threshold = 0;;
        myPopup() {
            //super();

            //  removePanel = new JPopupMenu();

            JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Time Column Mode");
            cbMenuItem.addActionListener(aListener);
            this.add(cbMenuItem);
            this.addSeparator();
//            JMenuItem menuItem = new JMenuItem("Remove");
//            menuItem.addActionListener(bListener);
//            this.add(menuItem);
//            this.addSeparator();
           
            
            
//             cbEventMenuItem = new JCheckBoxMenuItem("Show Event Mode");
//            cbEventMenuItem.addActionListener(eListener);
//            this.add(cbEventMenuItem);
//            this.addSeparator();
            
            
            
            JPanel subPanel = new JPanel(new BorderLayout());
              subPanel.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT);
            this.add(subPanel);
            eventParaSlider = new JSlider();
            
            subPanel.add(eventParaSlider, BorderLayout.LINE_START);
            
            eventParaSlider.setValue(40);
            thresholdLabel = new JLabel();
            
            
            subPanel.add(thresholdLabel,BorderLayout.LINE_END);
            thresholdLabel.setText(String.valueOf(2.0));
            eventParaSlider.addChangeListener(thresholdListener);
            
             this.addSeparator();
             
             JMenuItem menuItemEvent = new JMenuItem("Detect Events");

            menuItemEvent.addActionListener(eventListener);
            this.add(menuItemEvent);
        
           

        }

        public void setCurrentPanelString(String s) {
            this.currentPanelString = s;

        }

      
        
         ChangeListener thresholdListener = new ChangeListener(){
            
            public void stateChanged(ChangeEvent e) {
                
                eventParaSliderStateChanged(e);
                
            }
        };
        
         
         private void eventParaSliderStateChanged(ChangeEvent e)
         {
                     

            threshold = eventParaSlider.getValue() / 20.0f;

            // Update the label
            this.thresholdLabel.setText(String.valueOf(threshold));
             
             
         }
        
        
        ActionListener eventListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                
                //attachedPanel.setbShowEvents(!attachedPanel.isbShowEvents());
             
                
                attachedPanel.computeEventOutlineArea();


            }
        };
        
               ActionListener eListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();

     
              
                 attachedPanel.computeEventOutlineArea();

            }
        };
        
        
        ActionListener aListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AbstractButton aButton = (AbstractButton) event.getSource();
                boolean selected = aButton.getModel().isSelected();

           


            }
        };
        ActionListener bListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) (e.getSource());
                //System.out.print(source);


      

                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
