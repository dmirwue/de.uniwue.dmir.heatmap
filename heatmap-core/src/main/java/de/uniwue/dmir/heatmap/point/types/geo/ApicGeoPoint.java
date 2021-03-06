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
package de.uniwue.dmir.heatmap.point.types.geo;

import java.util.Date;

import de.uniwue.dmir.heatmap.point.sources.geo.GeoCoordinates;
import de.uniwue.dmir.heatmap.point.types.IGeoPoint;
import de.uniwue.dmir.heatmap.point.types.IValuePoint;
import lombok.Data;

@Data
public class ApicGeoPoint
implements IGeoPoint, IValuePoint {
	
	private long id;
	
	private GeoCoordinates geoCoordinates;
	private String geoProvider;
	
	private Date timestampReceived;
	private Date timestampRecorded;
	
	private String mac;
	private String deviceId;
	private String userId;

	private double value;
}
