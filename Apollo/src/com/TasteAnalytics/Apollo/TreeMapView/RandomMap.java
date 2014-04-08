
package com.TasteAnalytics.Apollo.TreeMapView;
import java.util.*;

/**
 * A random map model useful for testing layout algorithms.
 */
public class RandomMap implements MapModel {
    Mappable[] items;

    
 
     public RandomMap(Mappable[] i) {
	items = i;
    }
     
    public RandomMap(int length) {
	init(-1, 100, length);
    }

    public RandomMap(int seed, int size, int length) {
	init(seed, size, length);
    }

    void init(int seed, int size, int length) {
	double s;
	Random random;

	if (seed == -1) {
	    random = new Random();
	} else {
	    random = new Random(seed);
	}

	items = new MapItem[length];
	for (int i=0; i<length; i++) {
	    s=.05*(1+i);
	    items[i] = new MapItem(s, i);
	}
    }

    public Mappable[] getItems() {
	return items;
    }
}
