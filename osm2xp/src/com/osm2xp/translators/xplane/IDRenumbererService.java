package com.osm2xp.translators.xplane;

import java.util.HashMap;
import java.util.Map;

/**
 * Id renumberer to match new int ids starting from 1 with OSM node long ids
 * <b>IMPORTANT</b> this class is for single thread use only for now. Otherwise results can be unpredictable.
 * @author 32kda
 */
public class IDRenumbererService {
	
	private static Map<Long, Integer> crossingRenumberMap = new HashMap<Long, Integer>();
	private static int renumberCounter = -1; //We use negative values for "fake" nodes here because of https://wiki.openstreetmap.org/wiki/Node, "Editors may temporarily save node ids as negative to denote ids that haven't yet been saved to the server."
	
	public static void reinit() {
		crossingRenumberMap.clear();
		renumberCounter = -1;
	}
	
	public static int getNewId(long id) {
		Integer newId = crossingRenumberMap.get(id);
		if (newId == null) {
			newId = renumberCounter;
			renumberCounter--;
			crossingRenumberMap.put(id,newId);
		}
		return newId;
	}
	
	/**
	 * Return new id, actually not increment, but decrement starting at -1, see reason above 
	 * @return
	 */
	public static int getIncrementId() {
		int newId = renumberCounter;
		renumberCounter--;
		return newId;
	}
	
}
