/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mediavirus.parvis.gui.temporalView.renderer;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.mediavirus.parvis.gui.ViewController;
import org.mediavirus.parvis.gui.temporalView.CategoryBarElement;

/**
 *
 * @author Li
 */
public class ZoomedTemporalViewPanel extends TemporalViewPanel {

    ViewController parent;

    public ZoomedTemporalViewPanel(ViewController vc) throws IOException {
        super(vc);
        parent = vc;

        TemporalViewInteractions interactions = new TemporalViewInteractions(this);
        addMouseListener(interactions);
        addMouseMotionListener(interactions);

    }
    
    List<Integer> docIdx = new ArrayList<Integer>();

    public List<Integer> getDocIdx() {
        return docIdx;
    }

    public void setDocIdx(List<Integer> docIdx) {
        this.docIdx = docIdx;
    }
    
    
    HashMap<Integer, List<Integer>> documentInThisPanel = new HashMap<Integer, List<Integer>>();

    public HashMap<Integer, List<Integer>> getDocumentInThisPanel() {
        return documentInThisPanel;
    }

    public void setDocumentInThisPanel(HashMap<Integer, List<Integer>> documentInThisPanel) {
        this.documentInThisPanel = documentInThisPanel;
    }
    
    
    

    @Override
    protected void drawTimeLine(Graphics2D g2d) {

        g2d.setColor(Color.LIGHT_GRAY);
        // g2d.fillRect(0, 0, width, margin / 2);
        g2d.fillRect(0, height - (0 / 2), width, 0 / 2);


        int number = parent.getTemporalFrame().getData().getNumOfTemporalBinsSub();

        float ratio = width / number;

        
            SimpleDateFormat f = (SimpleDateFormat) parent.getFormat();
            String intervalString = null;
             if (f.toPattern() == "yyyy") {
            intervalString = parent.getTemporalFrame().getData().getHr2ms().toString() + "Year";

        } else {

            long millis = parent.getTemporalFrame().getData().getSub_timeInterval();
            long days = TimeUnit.MILLISECONDS.toDays(millis);
            
            long hrs = TimeUnit.MILLISECONDS.toHours(millis);//toDays();
            
            if (days > 0)
                intervalString = days + " day(s)" ;
            else
                intervalString =  hrs + " hr(s)";
        }

            
            g2d.drawString(intervalString, 10, 0 + height / 20);
        g2d.setColor(Color.BLACK);
        
        for (int i=0; i<number ; i++)
        {
            Long tmpMili = parent.getTemporalFrame().getData().getSubStartTime() + parent.getTemporalFrame().getData().getSub_timeInterval() * i;
        

                String lowerstr;

                if (f.toPattern() == "yyyy") {
                    lowerstr = tmpMili.toString();

                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(f.toPattern());
                    //SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy");
                    lowerstr = sdf.format(new Date(tmpMili));//Integer.toString(data.getBeginningYear()+i);//TimeUtils.returnDate(timecolumns.get(i).getNearTime(), data.getData().get(i).getDateFormatter());
                }
                
                //lowerstr = "Start time: " +lowerstr ;
                
                  g2d.drawString(lowerstr, (int) ((i) * ratio), height - height / 20);
         }       
        for (int i = 0; i < number; i++) {
            
            // Upper Boundary
            g2d.drawLine((int) ((i + 1) * ratio), 0, (int) ((i + 1) * ratio), height / 20);

            // Lower Bondary
            g2d.drawLine((int) ((i + 1) * ratio), height, (int) ((i + 1) * ratio), height - height / 15);

        }
    }
}
