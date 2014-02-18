/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TasteAnalytics.HierarchicalTopics.topicRenderer;


//import java.util.Timer;
//import java.util.TimerTask;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import com.TasteAnalytics.HierarchicalTopics.gui.ViewController;
import com.TasteAnalytics.HierarchicalTopics.topicRenderer.BackgroundPanel;

/**
 *
 * @author Li
 */
public class VastGeoFrame extends javax.swing.JFrame {

    /**
     * Creates new form VastGeoFrame
     */
    ViewController parent;
    //List<JLabel> imgPreviewLabel;

    public class ImageLabel extends JLabel
    {
        boolean selected = false;
        BufferedImage myImage = null;
        public ImageLabel(BufferedImage I)
        {
            this.setIcon(new ImageIcon(I));
            myImage = I;
            this.setSize(500, 250);
            this.setBorder(BorderFactory.createLineBorder(Color.black));
                        
        }
        
           
            public void update() {

                //this.selected = !this.selected;
                if (this.selected)
                    this.setBorder(BorderFactory.createLineBorder(Color.red));
                else
                    this.setBorder(BorderFactory.createLineBorder(Color.black));
                    
                
                    background.setOverlapImage(myImage);
                    background.repaint();

                }
                            
    }
    
    
    List<ImageLabel> imgPreviewLabel;
    
    public void setHeatmapImgPack(List<BufferedImage> heatmapImg) {
        this.heatmapImgPack = heatmapImg;

        imgPreviewLabel.clear();
        

        GridLayout experimentLayout = new GridLayout(1, heatmapImgPack.size());
        experimentLayout.setHgap(10);

        imgPreviewPanel.removeAll();
        
        imgPreviewPanel.setSize(heatmapImgPack.size() * 550, 300);
        imgPreviewPanel.setPreferredSize(new Dimension(heatmapImgPack.size() * 550, 300));
        //imgPreviewPanel.setSize(heatmapImgPack.size() * 550, 300);
        imgPreviewPanel.setLayout(experimentLayout);

        for (int i = 0; i < heatmapImgPack.size(); i++) {
            final ImageLabel Label = new ImageLabel(heatmapImgPack.get(i));

            Label.selected = false;
            Label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    //Label.update();  
                    
                    for (ImageLabel o : imgPreviewLabel)
                    {
//                        if ( o.equals(Label))
//                            continue;
//                        
                        o.selected = false;
                        o.update();                        
                    }
                    Label.selected = true;
                    
                    Label.update();

                }
            });

            imgPreviewLabel.add(Label);
            imgPreviewPanel.add(Label, 0, i);
        }


        imgPreviewPanel.revalidate();

    }
    List<BufferedImage> heatmapImgPack = new ArrayList<BufferedImage>();
    BufferedImage img = null;
    BufferedImage heatmapImg = null;
    JScrollPane scrollPane;
        
    public void setHeatmapImg(BufferedImage heatmapImg) {
        this.heatmapImg = heatmapImg;
        background.setOverlapImage(heatmapImg);
        background.repaint();
        //this.repaint();
    }
    BackgroundPanel background;

    public VastGeoFrame() {
        initComponents();
    }
    private JPanel imgPreviewPanel = new JPanel();

    public JPanel getImgPreviewPanel() {
        return imgPreviewPanel;
    }
    
    int current = 0;
    

    Timer timer;
    
    
    public VastGeoFrame(ViewController vc, String csvFilePath, List<Point2D> geoLocations) {
        initComponents();
        this.setTitle("Geo Spatial View");
        parent = vc;


        imgPreviewLabel = new ArrayList<ImageLabel>();
        

        try {
            File f = new File(csvFilePath + "Vastopolis_Map.png");
            img = ImageIO.read(f);
            //System.out.println("File " + f.toString());
        } catch (Exception e) {
            System.out.println("Cannot read file: " + e);
            this.setVisible(false);
        }

        background = new BackgroundPanel(img, BackgroundPanel.SCALED, 0, 0);

        this.setSize(1200, 900);
        this.setLayout(new BorderLayout());
       
        JButton button = new JButton("PLAY THROUGH");

        ButtonListener playListener = new ButtonListener();
//            ActionListener playListener = new ActionListener() {
//             public void actionPerformed(ActionEvent e) {
//            System.out.println("You clicked the button");                                 
//           
//                   if (heatmapImgPack.size()>current)
//                   {
//                        System.out.println("run");
//                    background.setOverlapImage(heatmapImgPack.get(current));                    
//                    background.repaint();                                                                                       
//                     current++;
//                   }
//                   else
//                   {
////                       System.out.println("stop");
////                       timer.stop();
//                       //timer.cancel();
//                       
//                   }
//                   
//                   
//                   
////            for (int i = 0; i < heatmapImgPack.size(); i++) {
////                   
////                    background.setOverlapImage(heatmapImgPack.get(i));                    
////                    background.repaint();
////                                                                            
////            }
//        }
//        
//        
//    };
            
            timer = new Timer(1000, playListener) ;
        button.addActionListener(playListener);
         
        
        
//        
//        if (current == 0) {
//            timer.start();
//        }
//                             
                             
                             
//        new ActionListener() {
//        public void actionPerformed(ActionEvent e) {
//            System.out.println("You clicked the button");
//             
//              Timer timer = new Timer();
//              timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    
//                   //if (!heatmapImgPack.isEmpty())
//                   {
//                       System.out.println("run");
//                    background.setOverlapImage(heatmapImgPack.get(current));                    
//                    background.repaint();                                                                                       
//                    current++;
//                   }
//                    
//                  // Your database code here
//                }
//              }, 1000);                            
  
//           
//
//    }});

        this.getContentPane().add(button, BorderLayout.PAGE_START);

        scrollPane = new JScrollPane(imgPreviewPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(imgPreviewPanel);
        //scrollPane.setPreferredSize(new Dimension(1000, 350));

//         JButton button = new JButton("Button 1 (PAGE_START)");
//         this.getContentPane().add(button, BorderLayout.LINE_START);


        imgPreviewPanel.setPreferredSize(new Dimension(1000, 300));
        // testPanel.setLayout(null);
        imgPreviewPanel.setBackground(Color.lightGray);




        //jDisplayPanel.setBounds(100,50,1000,500);
        jDisplayPanel.setPreferredSize(new Dimension(1200, 600));
        jDisplayPanel.add(background);

        this.getContentPane().add(jDisplayPanel, BorderLayout.PAGE_END);
//      this.setContentPane(background);     
//      button = new JButton("5 (LINE_END)");
//      this.getContentPane().add(button, BorderLayout.LINE_END);
//         


        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        
        
         this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {


                ((VastGeoFrame) e.getComponent()).setSize(new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height));
                scrollPane.setPreferredSize((new Dimension(e.getComponent().getSize().width, 350)));
                jDisplayPanel.setPreferredSize((new Dimension(e.getComponent().getSize().width, e.getComponent().getSize().height-350)));
                        
                        
                ((VastGeoFrame) e.getComponent()).invalidate();
            }

            public void componentMoved(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentShown(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void componentHidden(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    public int currentImg = 0;
    int delay = 1000;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDisplayPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jDisplayPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1343, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jDisplayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VastGeoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VastGeoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VastGeoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VastGeoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VastGeoFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jDisplayPanel;
    // End of variables declaration//GEN-END:variables










public class ButtonListener implements ActionListener
     {

        public void actionPerformed(ActionEvent e) {
           // System.out.println("You clicked the button");                                 
           
            
                    if (current == 0)
                        timer.start();
                    
                    if (current>=heatmapImgPack.size())
                    {
                         System.out.println("current image " + current + "timer stop") ;
                        timer.stop(); 
                        current = 0;
                        return;
                    }

                   if (heatmapImgPack.size()>current)
                   {
                        //System.out.println("run");
                        background.setOverlapImage(heatmapImgPack.get(current));                    
                        background.repaint();                                                                                       
                        current++;
                   }
                   
                   
                   System.out.println("current image " + current);
                   
//            for (int i = 0; i < heatmapImgPack.size(); i++) {
//                   
//                    background.setOverlapImage(heatmapImgPack.get(i));                    
//                    background.repaint();
//                                                                            
//            }
        }
            
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
         
     


}
