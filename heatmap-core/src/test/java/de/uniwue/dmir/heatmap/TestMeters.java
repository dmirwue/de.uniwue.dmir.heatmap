/**
 * Heatmap Framework - Core
 *
 * Copyright (C) 2013	Martin Becker
 * 						becker@informatik.uni-wuerzburg.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package de.uniwue.dmir.heatmap;

import de.uniwue.dmir.heatmap.DefaultZoomLevelMapper;
import de.uniwue.dmir.heatmap.IZoomLevelMapper;
import de.uniwue.dmir.heatmap.TileSize;
import de.uniwue.dmir.heatmap.filters.AbstractConfigurableFilter;
import de.uniwue.dmir.heatmap.point.sources.geo.GeoBoundingBox;
import de.uniwue.dmir.heatmap.point.sources.geo.GeoCoordinates;
import de.uniwue.dmir.heatmap.point.sources.geo.projections.MercatorMapProjection;
import de.uniwue.dmir.heatmap.processors.visualizers.rbf.IDistanceFunction;
import de.uniwue.dmir.heatmap.processors.visualizers.rbf.distances.GreatCircleDistance;
import de.uniwue.dmir.heatmap.processors.visualizers.rbf.distances.GreatCircleDistance.Cosine;
import de.uniwue.dmir.heatmap.processors.visualizers.rbf.distances.GreatCircleDistance.EquidistantApproximation;
import de.uniwue.dmir.heatmap.processors.visualizers.rbf.distances.GreatCircleDistance.Haversine;
import de.uniwue.dmir.heatmap.tiles.coordinates.TileCoordinates;

public class TestMeters {
	
	public static void main(String[] args) {
		
		int zoomLevel = 3;
		TileSize tileSize = new TileSize(1, 1);
		
		IZoomLevelMapper zoomLevelMapper = new DefaultZoomLevelMapper();
		
		MercatorMapProjection projection = new MercatorMapProjection(
				tileSize, zoomLevelMapper);
		
		GeoBoundingBox bb = projection.fromTileCoordinatesToGeoBoundingBox(
				new TileCoordinates(
						(int) zoomLevelMapper.getSize(zoomLevel).getWidth() / 2, 
						(int) zoomLevelMapper.getSize(zoomLevel).getHeight() / 2 + 1, 
						zoomLevel), 
				new AbstractConfigurableFilter<Object, Object>() {
					@Override
					public void filter(Object dataPoint, Object tile, TileSize tileSize, TileCoordinates tileCoordinates) {
					}
				});
		System.out.println(bb);
		
		GeoCoordinates northWest = new GeoCoordinates(
				bb.getMin().getLongitude(),
				bb.getMax().getLatitude());
		GeoCoordinates southEast = new GeoCoordinates(
				bb.getMax().getLongitude(),
				bb.getMin().getLatitude());
	
//		GeoCoordinates northEast = new GeoCoordinates(southEast.getLongitude(), northWest.getLatitude());
		GeoCoordinates southWest = new GeoCoordinates(northWest.getLongitude(), southEast.getLatitude());

		System.out.println(northWest);
		System.out.println(southWest);
		System.out.println(southEast);
		
		IDistanceFunction<GeoCoordinates> distance = 	new EquidistantApproximation();
		IDistanceFunction<GeoCoordinates> cosine = 		new Cosine();
		IDistanceFunction<GeoCoordinates> haversine = 	new Haversine();
		
		System.out.println(distance.distance(northWest, southWest)/ tileSize.getWidth());
		System.out.println(distance.distance(southWest, southEast)/ tileSize.getHeight());
		System.out.println("---");
		System.out.println(cosine.distance(northWest, southWest)/ tileSize.getWidth());
		System.out.println(cosine.distance(southWest, southEast)/ tileSize.getHeight());
		System.out.println("---");
		System.out.println(haversine.distance(northWest, southWest)/ tileSize.getWidth());
		System.out.println(haversine.distance(southWest, southEast)/ tileSize.getHeight());
		System.out.println("---");
	
		GeoCoordinates frankfurt = new GeoCoordinates(8.680506, 50.111511);
		GeoCoordinates munich = new GeoCoordinates(11.580186, 48.139126);

		System.out.println(distance.distance(frankfurt, munich));
		System.out.println(cosine.distance(frankfurt, munich));
		System.out.println(haversine.distance(frankfurt, munich));
		
		System.out.println(2 * Math.PI * GreatCircleDistance.EARTH_RADIUS / zoomLevelMapper.getSize(18).getWidth());
	}
	

}
