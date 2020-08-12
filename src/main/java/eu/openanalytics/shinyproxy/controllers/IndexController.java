/**
 * ShinyProxy
 *
 * Copyright (C) 2016-2019 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.shinyproxy.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import eu.openanalytics.containerproxy.model.spec.ProxySpec;
 
@Controller
public class IndexController extends BaseController {
	
	@RequestMapping("/")
    private Object index(ModelMap map, HttpServletRequest request) {
		String landingPage = environment.getProperty("proxy.landing-page", "/");
		if (!landingPage.equals("/")) return new RedirectView(landingPage);	
		
		prepareMap(map, request);
		
		ProxySpec[] apps = proxyService.getProxySpecs(null, false).toArray(new ProxySpec[0]);
		map.put("apps", apps);

		Map<ProxySpec, String> appLogos = new HashMap<>();
		map.put("appLogos", appLogos);
		
		List<String> potential_categories = new ArrayList<String>();
		potential_categories.add("player");
		potential_categories.add("team");
		potential_categories.add("draft");
		potential_categories.add("scouting");
		potential_categories.add("transactions");
		potential_categories.add("miscellaneous");

	 	List<String> categories = new ArrayList<String>(potential_categories);
		List<String> user_categories = new ArrayList<String>();
		
		boolean displayAppLogos = false;
		for (ProxySpec app: apps) {
		  if (!user_categories.contains(app.getDescription())) {
				user_categories.add(app.getDescription());
			}
			if (app.getLogoURL() != null) {
				displayAppLogos = true;
				appLogos.put(app, resolveImageURI(app.getLogoURL()));
			}
		}
		map.put("displayAppLogos", displayAppLogos);
		
		for (String cat: potential_categories) {
			if (!user_categories.contains(cat)) {
				categories.remove(cat);
			}
		}
			
		map.put("appCategories", categories);

		return "index";
    }
}