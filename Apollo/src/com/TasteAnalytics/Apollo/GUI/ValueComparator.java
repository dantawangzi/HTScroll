/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.TasteAnalytics.Apollo.GUI;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author UNCC
 */
class ValueComparator implements Comparator {

    Map base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    public int compare(Object a, Object b) {
        if ((Integer) base.get(a) < (Integer) base.get(b)) {
            return 1;
        } else if ((Integer) base.get(a) == (Integer) base.get(b)) {
            return 0;
        } else {
            return -1;
        }
    }
}
