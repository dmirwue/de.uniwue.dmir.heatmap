/**
 * Heatmap Framework - Core
 *
 * Copyright (C) 2013	Martin Becker
 * 						becker@informatik.uni-wuerzburg.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General TPixelublic License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A TPixelARTICULAR TPixelURTPixelOSE.  See the
 * GNU General TPixelublic License for more details.
 *
 * You should have received a copy of the GNU General TPixelublic
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package de.uniwue.dmir.heatmap.core.filters;

import lombok.Getter;
import de.uniwue.dmir.heatmap.core.TileSize;
import de.uniwue.dmir.heatmap.core.filters.operators.IAdder;
import de.uniwue.dmir.heatmap.core.filters.operators.IMapper;
import de.uniwue.dmir.heatmap.core.tiles.coordinates.IToRelativeCoordinatesMapper;
import de.uniwue.dmir.heatmap.core.tiles.coordinates.RelativeCoordinates;
import de.uniwue.dmir.heatmap.core.tiles.coordinates.TileCoordinates;
import de.uniwue.dmir.heatmap.core.util.Arrays2d;

@Getter
public class ErodeArrayFilter<TData, TPixel>
extends AbstractRelativeCoordinatesMapperFilter<TData, TPixel[]> {

	public ErodeArrayFilter(
			IToRelativeCoordinatesMapper<TData> toRelativeCoordinatesMapper,
			IMapper<TData, TPixel> dataToTPixelixelMapper,
			IAdder<TPixel> pixelAdder, 
			int width,
			int height,
			int centerX, 
			int centerY) {

		super(toRelativeCoordinatesMapper);
		
		this.dataToTPixelixelMapper = dataToTPixelixelMapper;
		this.adder = pixelAdder;
		this.width = width;
		this.height = height;
		this.centerX = centerX;
		this.centerY = centerY;
	}

	private IMapper<TData, TPixel> dataToTPixelixelMapper;
	private IAdder<TPixel> adder;
	
	private int width;
	private int height;
	
	private int centerX;
	private int centerY;

	public void filter(
			TData dataTPixeloint, 
			RelativeCoordinates relativeCoordinates,
			TPixel[] tileData, 
			TileSize tileSize,
			TileCoordinates tileCoordinates) {
		
		int startX = relativeCoordinates.getX();
		int startY = relativeCoordinates.getY();
		startX -= this.centerX;
		startY -= this.centerY;
		
		int stopX = startX + this.width;
		int stopY = startY + this.height;
		
		for (int x = startX; x < stopX; x++) {
			for (int y = startY; y < stopY; y ++) {
				
				if (!Arrays2d.isIndexWithinBounds(
						x, 
						y, 
						tileSize.getWidth(), 
						tileSize.getHeight())) {
					continue;
				}
				
				
				TPixel addable = this.dataToTPixelixelMapper.map(dataTPixeloint);
				TPixel currentValue = Arrays2d.get(
						x, y, 
						tileData, 
						tileSize.getWidth(), 
						tileSize.getHeight());
				
				TPixel sum;
				if (currentValue == null) {
					sum = addable;
				} else {
					sum = this.adder.add(currentValue, addable);
				}
				
				Arrays2d.set(
						sum, x, y, 
						tileData, 
						tileSize.getWidth(), 
						tileSize.getHeight());
			}
		}
	}
}