package com.osm2xp.translators.xplane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.osm2xp.core.model.osm.IHasTags;
import com.osm2xp.model.osm.polygon.OsmPolyline;
import com.osm2xp.translators.impl.XPOutputFormat;
import com.osm2xp.generation.options.GlobalOptionsProvider;
import com.osm2xp.generation.options.XPlaneOptionsProvider;
import com.osm2xp.writers.IWriter;

public class XPRoadTranslator extends XPPathTranslator {
	
	private static final String[] WIDE_ROAD_TYPES = {"motorway", "trunk", "primary", "secondary"}; 
	private static final String HIGHWAY_TAG = "highway";
	private String[] allowedHighwayTypes = GlobalOptionsProvider.getOptions().getAllowedHighwayTypesArray();
	private String[] allowedHighwayLinkTypes = GlobalOptionsProvider.getOptions().getAllowedHighwayLinkTypesArray();
	private String[] allowedHighwaySurfaceTypes = GlobalOptionsProvider.getOptions().getAllowedHighwaySurfaceTypesArray();
	public XPRoadTranslator(IWriter writer, XPOutputFormat outputFormat, IDRenumbererService idProvider) {
		super(writer, outputFormat, idProvider);
	}

	@Override
	public boolean handlePoly(OsmPolyline poly) {
		if (!XPlaneOptionsProvider.getOptions().isGenerateRoads()) {
			return false;
		}
		String highwayValue = poly.getTagValue(HIGHWAY_TAG);
		if (ArrayUtils.contains(allowedHighwayTypes, highwayValue) || ArrayUtils.contains(allowedHighwayLinkTypes, highwayValue) ) {
			String surface = poly.getTagValue("surface"); //Generate if surface type is either missing or among allowed values
			if (StringUtils.stripToEmpty(surface).trim().isEmpty() || ArrayUtils.contains(allowedHighwaySurfaceTypes, surface)) {
				addSegmentsFrom(poly);
				return true;
			}
		}
		return false;
	}

	/**
	 * Return X-Plane type constant for given road. Written in "hard" way to make some match between OSM tags and X-Plane type constants
	 * TODO Should me made configurable in future
	 * From https://forums.x-plane.org/index.php?/files/file/19074-roads-tutorial/ :
	 * 4 lanes - type 10
	 * 2 lanes - type 40
	 * 1 lane - type 50
	 */
	protected int getPathType(IHasTags poly) {
		String val = poly.getTagValue("lanes"); //Try to get lane count directly first
		if (val != null) {
			try {
				int value = Integer.parseInt(val.trim());
				if (value >= 4) {
					return 10;
				} else if (value >=2) {
					return 40;
				} else {
					return 50;
				}
			}catch (NumberFormatException e) {
				// ignore
			}
		}
//		String type = poly.getTagValue(HIGHWAY_TAG).toLowerCase(); //If no lane count - guess from the highway type
//		if (ArrayUtils.indexOf(WIDE_ROAD_TYPES, type) >= 0) {
//			return 40;
//		}
		if ("yes".equalsIgnoreCase(poly.getTagValue("oneWay")) && ArrayUtils.indexOf(WIDE_ROAD_TYPES, poly.getTagValue(HIGHWAY_TAG)) == -1) {
			return 50; //One-way road is treated as one-lane, if lane count is unspecified, and we doesn't have "wide" road	
		}
		return 40; //Use 2 lanes road by default
	}
	
	@Override
	protected int getBridgeRampLength() {
		return XPlaneOptionsProvider.getOptions().getRoadBridgeRampLen();
	}
	
	@Override
	public String getId() {
		return "road";
	}
}