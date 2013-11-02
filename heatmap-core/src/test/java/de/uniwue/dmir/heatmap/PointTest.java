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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.uniwue.dmir.heatmap.core.Heatmap;
import de.uniwue.dmir.heatmap.core.IFilter;
import de.uniwue.dmir.heatmap.core.IHeatmap;
import de.uniwue.dmir.heatmap.core.IHeatmap.HeatmapSettings;
import de.uniwue.dmir.heatmap.core.IHeatmap.TileSize;
import de.uniwue.dmir.heatmap.core.ITileFactory;
import de.uniwue.dmir.heatmap.core.MapTileFactory;
import de.uniwue.dmir.heatmap.core.MapTileFactory2;
import de.uniwue.dmir.heatmap.core.data.source.geo.GeoBoundingBox;
import de.uniwue.dmir.heatmap.core.data.source.geo.GeoCoordinates;
import de.uniwue.dmir.heatmap.core.data.source.geo.GeoTileDataSource;
import de.uniwue.dmir.heatmap.core.processing.DefaultFileStrategy;
import de.uniwue.dmir.heatmap.core.processing.GroupProxyFileWriter;
import de.uniwue.dmir.heatmap.core.processing.IToDoubleMapper;
import de.uniwue.dmir.heatmap.core.processing.PointProcessor;
import de.uniwue.dmir.heatmap.core.processing.VisualizationFileWriter;
import de.uniwue.dmir.heatmap.core.tile.coordinates.RelativeCoordinates;
import de.uniwue.dmir.heatmap.core.tile.coordinates.TileCoordinates;
import de.uniwue.dmir.heatmap.impl.core.data.source.geo.CsvGeoDataSource;
import de.uniwue.dmir.heatmap.impl.core.data.source.geo.EquidistantProjection;
import de.uniwue.dmir.heatmap.impl.core.data.source.geo.GeoPoint;
import de.uniwue.dmir.heatmap.impl.core.data.source.geo.GeoPointToGeoCoordinateMapper;
import de.uniwue.dmir.heatmap.impl.core.data.source.geo.GeoPointToGroupValuePixelMapper;
import de.uniwue.dmir.heatmap.impl.core.data.type.external.GroupValuePixel;
import de.uniwue.dmir.heatmap.impl.core.data.type.internal.PointSize;
import de.uniwue.dmir.heatmap.impl.core.filter.MapGroupAccess;
import de.uniwue.dmir.heatmap.impl.core.filter.MapPixelAccess;
import de.uniwue.dmir.heatmap.impl.core.filter.PointFilter;
import de.uniwue.dmir.heatmap.impl.core.filter.ProxyGroupFilter;
import de.uniwue.dmir.heatmap.impl.core.visualizer.ImageColorScheme;
import de.uniwue.dmir.heatmap.impl.core.visualizer.MapKeyValueIteratorFactory;
import de.uniwue.dmir.heatmap.impl.core.visualizer.SimpleVisualizer;
import de.uniwue.dmir.heatmap.impl.core.visualizer.colors.CombinedColorPipe;
import de.uniwue.dmir.heatmap.impl.core.visualizer.colors.SimpleColorPipe;
import de.uniwue.dmir.heatmap.impl.core.visualizer.rbf.GreatCircleDistance;

public class PointTest {

	public static final GeoBoundingBox TEST = new GeoBoundingBox(
			new GeoCoordinates(-30,  30), 
			new GeoCoordinates( 30, -30));
	
	public static final GeoBoundingBox LONDON = new GeoBoundingBox(
			new GeoCoordinates(-0.11825877463434153, 51.53025322524078),
			new GeoCoordinates(-0.0662972773659476, 51.505549554927526));
	
	@Test
	public void testHeatmap() throws IOException {
		
		HeatmapSettings settings = new HeatmapSettings();
		settings.getZoomLevelRange().setMax(0);
		
		GeoBoundingBox gbb = LONDON;
		System.out.println(
				new GreatCircleDistance.Haversine().distance(
						LONDON.getSouthEast(), 
						new GeoCoordinates(
								LONDON.getNorthWest().getLongitude(), 
								LONDON.getSouthEast().getLatitude())));
		
		TileSize tileSize = EquidistantProjection.getTileSize(
				10, 
				10, 
				true, 
				gbb, 
				new GreatCircleDistance.Haversine());
		System.out.println(tileSize);
		
		EquidistantProjection projection = new EquidistantProjection(
				gbb,
				tileSize);
		RelativeCoordinates r1 = 
				projection.fromGeoToRelativeCoordinates(LONDON.getNorthWest(), null);
		System.out.println(r1);
		
		RelativeCoordinates r2 = 
				projection.fromGeoToRelativeCoordinates(LONDON.getSouthEast(), null);
		System.out.println(r2);
		
		GeoTileDataSource<GeoPoint, GroupValuePixel> dataSouce =
				new GeoTileDataSource<GeoPoint, GroupValuePixel>(
						new CsvGeoDataSource(
								new File("src/test/resources/test_london.txt"),
								",",
								false), 
						projection, 
						new GeoPointToGeoCoordinateMapper(), 
						new GeoPointToGroupValuePixelMapper());
		
		IFilter<GroupValuePixel, Map<RelativeCoordinates, PointSize>> filter = 
				new PointFilter<Map<RelativeCoordinates,PointSize>>(
						new MapPixelAccess<PointSize>());

		ITileFactory<Map<RelativeCoordinates, PointSize>> tileFactory = 
				new MapTileFactory<PointSize>();
		
		IFilter<GroupValuePixel, Map<String, Map<RelativeCoordinates, PointSize>>> groupFilter = 
				new ProxyGroupFilter<
					GroupValuePixel, 
					Map<RelativeCoordinates, PointSize>, 
					Map<String, Map<RelativeCoordinates, PointSize>>>(
						new MapGroupAccess<Map<RelativeCoordinates, PointSize>>(tileFactory),
						filter);
		
		IHeatmap<Map<String, Map<RelativeCoordinates, PointSize>>> heatmap =
				new Heatmap<GroupValuePixel, Map<String, Map<RelativeCoordinates, PointSize>>>(
						new MapTileFactory2<String, Map<RelativeCoordinates, PointSize>>(),
						dataSouce, 
						groupFilter,
						settings);

		System.out.println(dataSouce.getData(new TileCoordinates(0, 0, 0), filter).next());
				
		Map<String, Map<RelativeCoordinates, PointSize>> tile = 
				heatmap.getTile(new TileCoordinates(0, 0, 0));
		System.out.println(tile);
		
		PointProcessor pointProcessor = new PointProcessor("out/points.json", "MD5");
		
		BufferedImage colorImage = ImageIO.read(
				new File("src/main/resources/color-schemes/classic.png"));
		double[] ranges = ImageColorScheme.ranges(1, 48, colorImage.getHeight());
		
		SimpleVisualizer<Map<RelativeCoordinates, PointSize>, PointSize> simpleVisualizer = 
				new SimpleVisualizer<Map<RelativeCoordinates,PointSize>, PointSize>(
						new MapKeyValueIteratorFactory<RelativeCoordinates, PointSize>(),
						new CombinedColorPipe<PointSize>(
								new SimpleColorPipe<PointSize>(
										new IToDoubleMapper<PointSize>() {
											@Override
											public Double map(PointSize object) {
												return object.getPoints();
											}
										},
										new ImageColorScheme(colorImage, ranges)),
								null));
		
		VisualizationFileWriter<Map<RelativeCoordinates, PointSize>> fileWriter =
				new VisualizationFileWriter<Map<RelativeCoordinates,PointSize>>("", new DefaultFileStrategy(), "png", simpleVisualizer);
		
		GroupProxyFileWriter<Map<RelativeCoordinates, PointSize>, Map<String, Map<RelativeCoordinates, PointSize>>> groupProxyFileWriter =
				new GroupProxyFileWriter<Map<RelativeCoordinates,PointSize>, Map<String,Map<RelativeCoordinates,PointSize>>>(
						new MapKeyValueIteratorFactory<String, Map<RelativeCoordinates,PointSize>>(), 
						fileWriter, 
						"out/groups");
		
		heatmap.processTiles(pointProcessor);
		heatmap.processTiles(groupProxyFileWriter);
		
	}
}
