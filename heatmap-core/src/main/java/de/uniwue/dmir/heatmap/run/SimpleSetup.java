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
package de.uniwue.dmir.heatmap.run;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import lombok.Data;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.uniwue.dmir.heatmap.DefaultTileSizeProvider;
import de.uniwue.dmir.heatmap.DefaultZoomLevelSizeProvider;
import de.uniwue.dmir.heatmap.IFilter;
import de.uniwue.dmir.heatmap.IHeatmap;
import de.uniwue.dmir.heatmap.IPointsource;
import de.uniwue.dmir.heatmap.ITileFactory;
import de.uniwue.dmir.heatmap.ITileProcessor;
import de.uniwue.dmir.heatmap.ITileSizeProvider;
import de.uniwue.dmir.heatmap.IVisualizer;
import de.uniwue.dmir.heatmap.IZoomLevelSizeProvider;
import de.uniwue.dmir.heatmap.ZoomLevelRange;
import de.uniwue.dmir.heatmap.filters.AddingFilter;
import de.uniwue.dmir.heatmap.filters.operators.IAdder;
import de.uniwue.dmir.heatmap.filters.operators.SumAdder;
import de.uniwue.dmir.heatmap.filters.pixelaccess.IPixelAccess;
import de.uniwue.dmir.heatmap.filters.pixelaccess.MapPixelAccess;
import de.uniwue.dmir.heatmap.filters.pointmappers.geo.SimpleGeoPointToSumPixelMapper;
import de.uniwue.dmir.heatmap.heatmaps.DefaultHeatmap;
import de.uniwue.dmir.heatmap.point.sources.GeoPointsource;
import de.uniwue.dmir.heatmap.point.sources.geo.GeoCoordinates;
import de.uniwue.dmir.heatmap.point.sources.geo.IGeoDatasource;
import de.uniwue.dmir.heatmap.point.sources.geo.IMapProjection;
import de.uniwue.dmir.heatmap.point.sources.geo.datasources.CsvGeoDatasource;
import de.uniwue.dmir.heatmap.point.sources.geo.datasources.RTreeGeoDatasource;
import de.uniwue.dmir.heatmap.point.sources.geo.projections.MercatorMapProjection;
import de.uniwue.dmir.heatmap.point.types.geo.GeoPointToGeoCoordinateMapper;
import de.uniwue.dmir.heatmap.point.types.geo.SimpleGeoPoint;
import de.uniwue.dmir.heatmap.processors.VisualizerFileWriterProcessor;
import de.uniwue.dmir.heatmap.processors.filestrategies.DefaultFileStrategy;
import de.uniwue.dmir.heatmap.processors.visualizers.SimpleVisualizer;
import de.uniwue.dmir.heatmap.processors.visualizers.color.CombinedColorPipe;
import de.uniwue.dmir.heatmap.processors.visualizers.color.CutpointColorScheme;
import de.uniwue.dmir.heatmap.processors.visualizers.color.SimpleColorPipe;
import de.uniwue.dmir.heatmap.tiles.coordinates.IToRelativeCoordinatesMapper;
import de.uniwue.dmir.heatmap.tiles.coordinates.RelativeCoordinates;
import de.uniwue.dmir.heatmap.tiles.coordinates.geo.GeoPointToRelativeCoordinatesMapper;
import de.uniwue.dmir.heatmap.tiles.factories.MapTileFactory;
import de.uniwue.dmir.heatmap.tiles.pixels.SumPixel;
import de.uniwue.dmir.heatmap.util.iterator.MapKeyValueIteratorFactory;
import de.uniwue.dmir.heatmap.util.mapper.IMapper;

public class SimpleSetup {
	
	@Data
	public static class Settings {
		
		@Option(
				name = "-csv", 
				usage = "CSV file to read (format: lon,lat,value)",
				required = true)
		private File csvFile;
		
		@Option(
				name = "-csvsep", 
				aliases = {"-csvSeparator"}, 
				usage = "Cseparator to split lines in CSV file")
		private String csvSeparator = ",";
		
		@Option(
				name = "-csvskip", 
				aliases = {"-csvSkipFirstLine"}, 
				usage = "whether to skip the first line of the CSV file")
		private boolean csvSkipFirstLine = false;
		
		@Option(
				name = "-out", 
				usage = "the output folder", 
				required = true)
		private File outputFolder;
		
		@Option(
				name = "-minZ", 
				aliases = {"-minZoomLevel"}, 
				usage = "")
		private int minZoomLevel = -1;
		
		@Option(
				name = "-maxZ", 
				aliases = {"-maxZoomLevel"}, 
				usage = "")
		private int maxZoomLevel = -1;
		
		@Option(
				name = "-cut", 
				aliases = {"-cutpoints"}, 
				usage = "")
		private Double[] cutpoints = null;
		
		@Option(
				name = "-cols", 
				aliases = {"-colors"}, 
				usage = "")
		private String[] colors = null;
		
		
	}
	
	public static void main(String[] args) throws IOException {
		
		Settings settings = new Settings();
		CmdLineParser parser = new CmdLineParser(settings);
		
		try {
			parser.parseArgument(args);
		} catch(CmdLineException e ) {
			System.err.println(e.getMessage());
			System.err.println("java -jar myprogram.jar [options...] arguments...");
			parser.printUsage(System.err);
			return;
		}
		
		test(
				settings.getCsvFile(),
				settings.getCsvSeparator(),
				settings.isCsvSkipFirstLine(),
				settings.getOutputFolder().getAbsolutePath(),
				settings);
		
		
	}
	

	public static void test(
			File file, 
			String separator, 
			boolean skipFirstLine, 
			String outputParentFolder,
			Settings settings) throws IOException {
		
		// heatmap settings
		
		ZoomLevelRange zoomLevelRange = new ZoomLevelRange();
		if (settings.getMinZoomLevel() > -1) {
			zoomLevelRange.setMin(settings.getMinZoomLevel());
		}
		if (settings.getMaxZoomLevel() > -1) {
			zoomLevelRange.setMax(settings.getMaxZoomLevel());
		}
		
		IZoomLevelSizeProvider zoomLevelSizeProvider =
				new DefaultZoomLevelSizeProvider();
		
		ITileSizeProvider tileSizeProvider =
				new DefaultTileSizeProvider();
		
		
		
		// settings up point source
		
		IMapper<SimpleGeoPoint<String>, GeoCoordinates> pointToGeoCoordinatesMapper =
				new GeoPointToGeoCoordinateMapper<SimpleGeoPoint<String>>();
		
		// TODO: more flexible geo data source
		IGeoDatasource<SimpleGeoPoint<String>, Object> datasource = 
				new RTreeGeoDatasource<SimpleGeoPoint<String>, Object>(
						new CsvGeoDatasource(file, separator, skipFirstLine),
						pointToGeoCoordinatesMapper);
		
		IMapProjection mapProjection = new MercatorMapProjection(
				tileSizeProvider, 
				zoomLevelSizeProvider);
		
		IPointsource<SimpleGeoPoint<String>, Object> pointsource = new GeoPointsource<SimpleGeoPoint<String>, Object>(
				datasource, 
				mapProjection, 
				pointToGeoCoordinatesMapper);
		
		
		
		// the tile factory
		
		ITileFactory<Map<RelativeCoordinates, SumPixel>> tileFactory = 
				new MapTileFactory<RelativeCoordinates, SumPixel>();
		
		
		
		// the filter incorporating points into tiles
		
		IToRelativeCoordinatesMapper<SimpleGeoPoint<String>> dataToRelativeCoordinatesMapper =
				new GeoPointToRelativeCoordinatesMapper<SimpleGeoPoint<String>>(mapProjection);
		IMapper<SimpleGeoPoint<String>, SumPixel> dataToPixelMapper = 
				new SimpleGeoPointToSumPixelMapper<String>();
		IPixelAccess<SumPixel, Map<RelativeCoordinates, SumPixel>> pixelAccess = 
				new MapPixelAccess<SumPixel>();
		IAdder<SumPixel> pixelAdder = new SumAdder();
				
		AddingFilter<SimpleGeoPoint<String>, SumPixel, Map<RelativeCoordinates, SumPixel>> addingFilter = 
				new AddingFilter<SimpleGeoPoint<String>, SumPixel, Map<RelativeCoordinates, SumPixel>>(
						dataToRelativeCoordinatesMapper,
						dataToPixelMapper,
						pixelAccess, 
						pixelAdder);
		addingFilter.setWidth(220);
		addingFilter.setHeight(220);
		addingFilter.setCenterX(110);
		addingFilter.setCenterY(110);
		
		IFilter<SimpleGeoPoint<String>, Map<RelativeCoordinates, SumPixel>> filter = addingFilter;
		
		// the heatmap putting things together
		
		IHeatmap<Map<RelativeCoordinates, SumPixel>, Object> heatmap = 
					new DefaultHeatmap<SimpleGeoPoint<String>, Map<RelativeCoordinates, SumPixel>, Object>(
				tileFactory,
				pointsource,
				filter,
				tileSizeProvider,
				mapProjection);
		
		// the processor processing each tile:
		// simple visualizer
		
		Double[] cutpoints = new Double[] {
				1.,
				10.,
				100.,
				1000.
		};
		
		Color[] colors = new Color[] {
				Color.BLACK,
				Color.YELLOW,
				Color.ORANGE,
				Color.RED, 
				Color.WHITE
		};
		
		if (settings.getCutpoints() != null && settings.getColors() != null) {
				
			cutpoints = settings.getCutpoints();
			String[] stringColors = settings.getColors();
				
			if (cutpoints.length + 1 != stringColors.length) {
				throw new IllegalArgumentException(
						"There must be exactly one more color than cutpoints.");
			}
			
			colors = new Color[stringColors.length];
			for (int i = 0; i < stringColors.length; i++) {
				colors[i] = Color.decode(stringColors[i]);
			}
				
		} else if (settings.getCutpoints() != null || settings.getColors() != null) {
			
			throw new IllegalArgumentException(
					"Either none or both, cutpoints and colors, must be defined.");
			
		}
		
		IVisualizer<Map<RelativeCoordinates, SumPixel>> simpleVisualizer = 
				new SimpleVisualizer<Map<RelativeCoordinates, SumPixel>, SumPixel>(
						tileSizeProvider,
						new MapKeyValueIteratorFactory<RelativeCoordinates, SumPixel>(),
						new CombinedColorPipe<SumPixel>(
								new SimpleColorPipe<SumPixel>(
										new IMapper<SumPixel, Double>() {
											@Override
											public <TDerived extends SumPixel> Double map(
													TDerived object) {
												return object.getSize();
											}
										},
										new CutpointColorScheme(
												cutpoints, 
												colors, 
												true)), 
						null));
		
		IVisualizer<Map<RelativeCoordinates, SumPixel>> visualizer = simpleVisualizer;
		
		ITileProcessor<Map<RelativeCoordinates, SumPixel>> visualizerProcessor =
				new VisualizerFileWriterProcessor<Map<RelativeCoordinates,SumPixel>>(
						outputParentFolder,
						new DefaultFileStrategy(),
						"png",
						visualizer);
		
		ITileProcessor<Map<RelativeCoordinates, SumPixel>> processor = visualizerProcessor;
		heatmap.processTiles(
				processor,
				zoomLevelRange,
				null,
				null);
	}
	
}