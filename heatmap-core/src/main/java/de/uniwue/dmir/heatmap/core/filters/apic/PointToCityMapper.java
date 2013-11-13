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
package de.uniwue.dmir.heatmap.core.filters.apic;

import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import de.uniwue.dmir.heatmap.core.data.sources.geo.data.types.ApicPoint;
import de.uniwue.dmir.heatmap.core.filters.operators.IMapper;

@AllArgsConstructor
public class PointToCityMapper 
implements IMapper<ApicPoint, String> {

	private Map<String, Path2D> areas;

	@Override
	public String map(ApicPoint object) {
		
		for (Entry<String, Path2D> e : this.areas.entrySet()) {
			double x = object.getGeoCoordinates().getLongitude();
			double y = object.getGeoCoordinates().getLatitude();
			
			if (e.getValue().contains(x, y)) {
				return e.getKey();
			}
		}
		
		return null;
	}
}