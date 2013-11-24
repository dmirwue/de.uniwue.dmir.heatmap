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
package de.uniwue.dmir.heatmap.processors;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniwue.dmir.heatmap.IVisualizer;
import de.uniwue.dmir.heatmap.TileSize;
import de.uniwue.dmir.heatmap.processors.filestrategies.IFileStrategy;
import de.uniwue.dmir.heatmap.tiles.coordinates.TileCoordinates;

public class VisualizerFileWriterProcessor<I> 
extends AbstractFileWriterProcessor<I> {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private IVisualizer<I> visualizer;
	
	public VisualizerFileWriterProcessor(
			String parentFolder,
			IFileStrategy fileStrategy, 
			String fileFormat, 
			IVisualizer<I> visualizer) {
		
		super(parentFolder, fileStrategy, fileFormat, false);
		this.visualizer = visualizer;
	}
	
	@Override
	public void process(I tile, TileSize tileSize, TileCoordinates coordinates) {
		
		if (tile == null) {
			return;
		}
		
		BufferedImage image = this.visualizer.visualize(tile, tileSize, coordinates);
		try {
			OutputStream outputStream = this.getOutputStream(coordinates);
			ImageIO.write(image, super.fileFormat, outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}