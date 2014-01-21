/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mediavirus.parvis.gui.temporalView.renderer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lyu8
 */
public class TemporalPanelTree {

    private TemporalPanelTree parent = null;
    private List children = null;
    private int index;
    private String label;

    public void setLabel(String mValue) {
        this.label = mValue;
    }

    public TemporalPanelTree(Object obj) {
        this.parent = null;
        this.children = new ArrayList();
        this.label = "";

    }

    private void removeChild(TemporalPanelTree child) {
        if (children.contains(child)) {
            children.remove(child);
        }

    }

    public void addChildNode(TemporalPanelTree child) {
        child.parent = this;
        if (!children.contains(child)) {
            children.add(child);
        }
    }

}


