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
package de.uniwue.dmir.heatmap.util.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import de.uniwue.dmir.heatmap.mybatis.MappingsMyBatisMapper;
import de.uniwue.dmir.heatmap.mybatis.MappingsMyBatisMapper.Element;

public class MapFactoryBean implements FactoryBean<Map<String, String>> {

	@Autowired
	private MappingsMyBatisMapper mappingsMyBatisMapper;
	
	private String mapIdentifier;
	
	public MapFactoryBean(String mapIdentifier) {
		this.mapIdentifier = mapIdentifier;
	}
	
	public MapFactoryBean(String mapIdentifier, MappingsMyBatisMapper mappingsMyBatisMapper) {
		this(mapIdentifier);
		this.mappingsMyBatisMapper = mappingsMyBatisMapper;
	}
	
	@Override
	public Map<String, String> getObject() throws Exception {
		
		List<Element> elements = 
				this.mappingsMyBatisMapper.getElements(
						this.mapIdentifier);
		
		Map<String, String> map = new HashMap<String, String>();
		for (Element e : elements) {
			map.put(e.getKey(), e.getValue());
		}
		
		return map;
	}

	@Override
	public Class<?> getObjectType() {
		return Map.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
