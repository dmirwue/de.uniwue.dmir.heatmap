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
package de.uniwue.dmir.heatmap;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.uniwue.dmir.heatmap.IHeatmap;
import de.uniwue.dmir.heatmap.ITileProcessor;

public class SpringTest3 {

	public static final String HEATMAP_BEAN = "heatmap";
	public static final String WRITER_BEAN = "writer";
	
//	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testHeatmap() throws IOException {
		

		System.setProperty("minTimestamp", "2013-06-01 00:00:00");
		System.setProperty("maxTimestamp", "2013-07-30 00:00:00");
		System.setProperty("workDir", "out/basic/work-jar");
		System.setProperty("configDir", "src/main/resources/spring/example/basic/config");
		
		ClassPathXmlApplicationContext appContext = 
				new ClassPathXmlApplicationContext(
						new String[] {"spring/example/basic/config/config.xml"},
						false);
		appContext.refresh();
		
		IHeatmap heatmap = 
				appContext.getBean(HEATMAP_BEAN, IHeatmap.class);

		ITileProcessor tileProcessor = 
				appContext.getBean(WRITER_BEAN, ITileProcessor.class);
		
		heatmap.processTiles(tileProcessor);
		
		tileProcessor.close();
		appContext.close();
	}
	
}
