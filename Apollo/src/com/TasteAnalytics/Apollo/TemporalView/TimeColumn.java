/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.TemporalView;
import java.awt.Rectangle;

/**
 *
 * @author xwang
 */
public class TimeColumn {
    private Long nearTime;
    private Long farTime;
    private Rectangle renderRegion;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private boolean isFocused;

    public boolean isIsFocused() {
        return isFocused;
    }

    public void setIsFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    public Rectangle getRenderRegion() {
        return renderRegion;
    }

    public void setRenderRegion(Rectangle renderRegion) {
        this.renderRegion = renderRegion;
    }

    public Long getFarTime() {
        return farTime;
    }

    public void setFarTime(Long farTime) {
        this.farTime = farTime;
    }

    public Long getNearTime() {
        return nearTime;
    }

    public void setNearTime(Long nearTime) {
        this.nearTime = nearTime;
    }

    public TimeColumn(int id, Long nearTime, Long farTime, double x, double y, double width, double height) {
        this.nearTime = nearTime;
        this.farTime = farTime;
        this.id = id;
        renderRegion = new Rectangle((int)x, (int)y, (int)width, (int)height);

        isFocused = false;
    }
    
}
