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
package de.uniwue.dmir.heatmap.impl.core.filter;

import java.awt.image.BufferedImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.uniwue.dmir.heatmap.core.IFilter;
import de.uniwue.dmir.heatmap.core.data.type.IExternalData;
import de.uniwue.dmir.heatmap.core.filter.operators.IAdder;
import de.uniwue.dmir.heatmap.core.filter.operators.IMapper;
import de.uniwue.dmir.heatmap.core.filter.operators.IScalarMultiplier;
import de.uniwue.dmir.heatmap.core.tile.ITile;
import de.uniwue.dmir.heatmap.core.util.Arrays2d;

@AllArgsConstructor
@Getter
public class ImageFilter<T extends IExternalData, P> 
implements IFilter<T, P>{

	private IMapper<T, P> mapper;
	private IAdder<P> adder;
	private IScalarMultiplier<P> multiplier;
	
	private int width;
	private int height;
	
	private int centerX;
	private int centerY;
	
	Double[] array;

	public ImageFilter(
			IMapper<T, P> mapper,
			IAdder<P> adder,
			IScalarMultiplier<P> multiplier,
			BufferedImage image) {
		
		this.mapper = mapper;
		this.adder = adder;
		this.multiplier = multiplier;
		
		if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
			throw new IllegalArgumentException(
					"Image must be a gray scale image.");
		}
		
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		this.centerX = this.width / 2;
		this.centerY = this.height / 2;
		
		// values and sum
		this.array = new Double[this.width * this.height];
		double max = 0;
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j ++) {
				double value = image.getData().getSampleDouble(i, j, 0);
				Arrays2d.set(value, i, j, this.array, this.width, this.height);
				max = Math.max(max, value);
			}
		}
		
		// normalize value wise
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j ++) {
				
				double value = Arrays2d.get(i, j, this.array, this.width, this.height);
				value /= max;
				
				Arrays2d.set(value, i, j, this.array, this.width, this.height);
			}
		}
	}
	
	public void filter(T dataPoint, ITile<T, P> tile) {
		
		P[] tileData = tile.getData();
		
		int startX = dataPoint.getCoordinates().getX();
		int startY = dataPoint.getCoordinates().getY();
		startX -= this.centerX;
		startY -= this.centerY;
		
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j ++) {
				
				int x = startX + i;
				int y = startY + j;

				if (!Arrays2d.checkIndex(
						x, 
						y, 
						tile.getDimensions().getWidth(), 
						tile.getDimensions().getHeight())) {
					continue;
				}
				
				P addable = this.mapper.map(dataPoint);
				
				double multiplicator = 
						Arrays2d.get(i, j, this.array, this.width, this.height);
				this.multiplier.multiply(addable, multiplicator);
				
				P currentValue = Arrays2d.get(
						x, y, 
						tileData, 
						tile.getDimensions().getWidth(), 
						tile.getDimensions().getHeight());
				
				P sum;
				if (currentValue == null) {
					sum = addable;
				} else {
					sum = this.adder.add(currentValue, addable);
				}
				
				Arrays2d.set(
						sum, x, y, 
						tileData, 
						tile.getDimensions().getWidth(), 
						tile.getDimensions().getHeight());
			}
		}
	}
}
