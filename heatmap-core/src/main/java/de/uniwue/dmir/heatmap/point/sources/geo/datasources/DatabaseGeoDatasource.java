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
package de.uniwue.dmir.heatmap.point.sources.geo.datasources;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;

import de.uniwue.dmir.heatmap.mybatis.GeoMapper;
import de.uniwue.dmir.heatmap.point.sources.geo.GeoBoundingBox;
import de.uniwue.dmir.heatmap.point.sources.geo.IGeoDatasource;
import de.uniwue.dmir.heatmap.point.sources.geo.datasources.database.GeoRequest;

public class DatabaseGeoDatasource<TData, TSettings>
implements IGeoDatasource<TData> {

	private GeoRequest<TSettings> geoRequest;
	
	@Autowired
	@Getter
	@Setter
	private GeoMapper<TData, TSettings> mapper;
	
	public DatabaseGeoDatasource(TSettings settings) {
		this.geoRequest = new GeoRequest<TSettings>();
		this.geoRequest.setSettings(settings);
	}
	
	@Override
	public List<TData> getData(GeoBoundingBox geoBoundingBox) {
		this.geoRequest.setGeoBoundingBox(geoBoundingBox);
		List<TData> points = this.mapper.getData(this.geoRequest);
		return points;
	}

}
