/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.internal;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.portlet.BaseURL;
import javax.portlet.faces.Bridge.PortletPhase;
import javax.portlet.faces.BridgeUtil;

import com.liferay.faces.bridge.config.BridgeConfig;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Neil Griffin
 */
public class BridgeURLBookmarkableImpl extends BridgeURLBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(BridgeURLBookmarkableImpl.class);

	public BridgeURLBookmarkableImpl(String uri, String contextPath, String namespace, String currentViewId,
		Map<String, List<String>> bookmarkParameters, BridgeConfig bridgeConfig) throws URISyntaxException {

		super(uri, contextPath, namespace, currentViewId, bridgeConfig);

		if ((bridgeURI != null) && (bridgeURI.toString() != null) && (bookmarkParameters != null)) {

			Map<String, String[]> parameterMap = getParameterMap();
			Set<Map.Entry<String, List<String>>> entrySet = bookmarkParameters.entrySet();

			for (Map.Entry<String, List<String>> mapEntry : entrySet) {

				String key = mapEntry.getKey();
				String[] valueArray = null;
				List<String> valueList = mapEntry.getValue();

				if (valueList != null) {
					valueArray = valueList.toArray(new String[valueList.size()]);
				}

				parameterMap.put(key, valueArray);
			}
		}
	}

	// Java 1.6+ @Override
	public BaseURL toBaseURL() throws MalformedURLException {

		BaseURL baseURL = null;

		// If this is executing during the RENDER_PHASE or RESOURCE_PHASE of the portlet lifecycle, then
		PortletPhase portletRequestPhase = BridgeUtil.getPortletRequestPhase();

		if ((portletRequestPhase == PortletPhase.RENDER_PHASE) ||
				(portletRequestPhase == PortletPhase.RESOURCE_PHASE)) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			baseURL = createRenderURL(facesContext, bridgeURI.getParameterMap());
			baseURL.setParameters(getParameterMap());
			baseURL.setParameter(viewIdRenderParameterName, currentViewId);
		}

		// Otherwise, log an error.
		else {
			logger.error("Unable to encode bookmarkable URL during Bridge.PortletPhase.[{0}].", portletRequestPhase);
		}

		return baseURL;
	}
}