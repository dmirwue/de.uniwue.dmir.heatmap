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
package de.uniwue.dmir.heatmap.filters.operators;

import de.uniwue.dmir.heatmap.tiles.pixels.WeightedSquaredSumPixel;


public class WeightedSquaredSumAdder
implements IAdder<WeightedSquaredSumPixel> {

	public WeightedSquaredSumPixel add(WeightedSquaredSumPixel o1, WeightedSquaredSumPixel o2) {
		
		WeightedSquaredSumPixel sum = new WeightedSquaredSumPixel(
				o1.getSize()						+ o2.getSize(),
				o1.getSumOfValues() 				+ o2.getSumOfValues(),
				o1.getSumOfSquaredValues() 			+ o2.getSumOfSquaredValues(),
				o1.getSumOfWeightedValues() 		+ o2.getSumOfWeightedValues(),
				o1.getSumOfWeightedSquaredValues() 	+ o2.getSumOfWeightedSquaredValues(),
				o1.getSumOfWeights() 				+ o2.getSumOfWeights());
		
		return sum;
	}
	
}