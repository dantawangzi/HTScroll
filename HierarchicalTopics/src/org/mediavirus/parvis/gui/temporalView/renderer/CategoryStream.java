/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mediavirus.parvis.gui.temporalView.renderer;

import java.awt.geom.Area;

/**
 *
 * @author xwang
 */
public class CategoryStream {

    private Area renderRegion;

    public Area getRenderRegion() {
        return renderRegion;
    }

    public void setRenderRegion(Area renderRegion) {
        this.renderRegion = renderRegion;
    }

    private boolean isHighlight;

    public boolean isIsHighlight() {
        return isHighlight;
    }

    public void setIsHighlight(boolean isHighlight) {
        this.isHighlight = isHighlight;
    }

    public CategoryStream(Area renderRegion) {
        this.renderRegion = renderRegion;
        this.isHighlight = false;
    }

}
