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
package de.uniwue.dmir.heatmap.util.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Allows to merge the output of to-{@link List} mappers.
 * 
 * @author Martin Becker
 *
 * @param <TSource>
 * @param <TListElement>
 */
public class ProxyListToUnionListMapper<TSource, TListElement>
implements IMapper<TSource, List<TListElement>> {

	private List<IMapper<TSource, List<TListElement>>> mappers;
	
	@Getter
	@Setter
	private boolean filterNullValues;
	
	@Getter
	@Setter
	private boolean addNullIfNullWasFound;
	
	public ProxyListToUnionListMapper(
			List<IMapper<TSource, List<TListElement>>> mappers) {
		
		this.mappers = mappers;
		this.filterNullValues = true;
		this.addNullIfNullWasFound = true;
	}
	
	@Override
	public <TDerived extends TSource> List<TListElement> map(TDerived object) {
		
		List<TListElement> union = new ArrayList<TListElement>();

		boolean nullWasFound = false;		
		for (IMapper<TSource, List<TListElement>> m : this.mappers) {
			
			List<TListElement> elements = m.map(object);
			
			// filter nulls
			if (this.filterNullValues) {
				
				Iterator<TListElement> iterator = elements.iterator();
				while (iterator.hasNext()) {
					TListElement element = iterator.next();
					if (element == null) {
						iterator.remove();
						nullWasFound = true;
					}
				}
				
			}
			
			union.addAll(elements);
		}
		
		if (this.addNullIfNullWasFound && nullWasFound) {
			union.add(null);
		}
		
		return union;
	}

}
