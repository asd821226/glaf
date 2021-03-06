/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glaf.core.web.springmvc;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.cache.CacheFactory;
import com.glaf.core.cache.CacheItem;
import com.glaf.core.cache.ClearCacheJob;
import com.glaf.core.config.ConfigFactory;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.factory.TableFactory;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.Constants;
import com.glaf.core.util.DateUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;

@Controller("/sys/cacheMgr")
@RequestMapping("/sys/cacheMgr")
public class SystemCacheMgrController {
	protected static final Log logger = LogFactory.getLog(SystemCacheMgrController.class);

	@RequestMapping("/clearAll")
	public ModelAndView clearAll(HttpServletRequest request, ModelMap modelMap) {
		ClearCacheJob job = new ClearCacheJob();
		try {
			logger.debug("#################CacheFactory.clearAll##################");
			job.clearAll();
		} catch (Exception ex) {

		}

		try {
			logger.debug("#################ConfigFactory.clearAll##################");
			ConfigFactory.clearAll();
		} catch (Exception ex) {

		}

		try {
			logger.debug("#################TableFactory.clear##################");
			TableFactory.clear();
		} catch (Exception ex) {

		}

		try {
			logger.debug("#################SystemConfig.reload##################");
			SystemConfig.reload();
		} catch (Exception ex) {

		}

		modelMap.put("reloadOK", true);

		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_cache.clearAll");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return this.list(request, modelMap);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public byte[] delete(HttpServletRequest request, ModelMap modelMap) {
		String region = request.getParameter("region");
		String rowIds = request.getParameter("keys");
		if (StringUtils.isNotEmpty(region) && StringUtils.isNotEmpty(rowIds)) {
			logger.debug("remove keys:" + rowIds);
			StringTokenizer token = new StringTokenizer(rowIds, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					CacheFactory.remove(region, x);
				}
			}
		}
		return ResponseUtils.responseJsonResult(true);
	}

	@RequestMapping("/detail")
	@ResponseBody
	public byte[] detail(HttpServletRequest request, ModelMap modelMap) throws IOException {
		JSONObject result = new JSONObject();
		String region = request.getParameter("region");
		String key = request.getParameter("key");
		String value = CacheFactory.getString(region, key);
		if (value != null) {
			result.put("size", value.length());
			result.put("value", value);
		}
		logger.debug("region:" + region + "\tkey:" + key);
		logger.debug("value:" + value);
		// logger.debug(result.toJSONString());
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String actorId = loginContext.getActorId();
		JSONObject result = new JSONObject();
		Collection<CacheItem> rows = CacheFactory.getCacheItems();
		if (rows != null && !rows.isEmpty()) {
			JSONArray rowsJSON = new JSONArray();
			Date date = null;
			int index = 0;
			String cacheKey = Constants.CACHE_LOGIN_CONTEXT_KEY + actorId;
			for (CacheItem item : rows) {
				if (StringUtils.equals(cacheKey, item.getName())) {
					continue;
				}
				if (StringUtils.equals(cacheKey, item.getKey())) {
					continue;
				}
				index++;
				JSONObject json = new JSONObject();
				json.put("index", index);
				json.put("region", item.getRegion());
				json.put("name", item.getName());
				json.put("key", item.getKey());
				json.put("size", item.getSize());
				date = new Date(item.getLastModified());
				json.put("date", DateUtils.getDateTime(date));
				rowsJSON.add(json);
			}
			result.put("total", rows.size());
			result.put("totalCount", rows.size());
			result.put("totalRecords", rows.size());
			result.put("start", 0);
			result.put("startIndex", 0);
			result.put("limit", rows.size());
			result.put("pageSize", rows.size());
			result.put("rows", rowsJSON);
		}
		return result.toString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_cache.list");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/sys/cache/list", modelMap);
	}

	@RequestMapping("/view")
	public ModelAndView view(HttpServletRequest request, ModelMap modelMap) {
		String key = request.getParameter("key");
		String region = request.getParameter("region");
		request.setAttribute("cacheValue", CacheFactory.getString(region, key));
		String jx_view = request.getParameter("jx_view");

		if (StringUtils.isNotEmpty(jx_view)) {
			return new ModelAndView(jx_view, modelMap);
		}

		String x_view = ViewProperties.getString("sys_cache.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/sys/cache/view", modelMap);
	}
}