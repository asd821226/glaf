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

package com.glaf.core.domain.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.util.DateUtils;
import com.glaf.core.domain.*;

/**
 * 
 * JSON工厂类
 *
 */
public class SchedulerExecutionJsonFactory {

	public static java.util.List<SchedulerExecution> arrayToList(JSONArray array) {
		java.util.List<SchedulerExecution> list = new java.util.ArrayList<SchedulerExecution>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			SchedulerExecution model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static SchedulerExecution jsonToObject(JSONObject jsonObject) {
		SchedulerExecution model = new SchedulerExecution();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getLong("id"));
		}
		if (jsonObject.containsKey("schedulerId")) {
			model.setSchedulerId(jsonObject.getString("schedulerId"));
		}
		if (jsonObject.containsKey("businessKey")) {
			model.setBusinessKey(jsonObject.getString("businessKey"));
		}
		if (jsonObject.containsKey("count")) {
			model.setCount(jsonObject.getInteger("count"));
		}
		if (jsonObject.containsKey("value")) {
			model.setValue(jsonObject.getDouble("value"));
		}
		if (jsonObject.containsKey("runYear")) {
			model.setRunYear(jsonObject.getInteger("runYear"));
		}
		if (jsonObject.containsKey("runMonth")) {
			model.setRunMonth(jsonObject.getInteger("runMonth"));
		}
		if (jsonObject.containsKey("runWeek")) {
			model.setRunWeek(jsonObject.getInteger("runWeek"));
		}
		if (jsonObject.containsKey("runQuarter")) {
			model.setRunQuarter(jsonObject.getInteger("runQuarter"));
		}
		if (jsonObject.containsKey("runDay")) {
			model.setRunDay(jsonObject.getInteger("runDay"));
		}
		if (jsonObject.containsKey("runTime")) {
			model.setRunTime(jsonObject.getInteger("runTime"));
		}
		if (jsonObject.containsKey("jobNo")) {
			model.setJobNo(jsonObject.getString("jobNo"));
		}
		if (jsonObject.containsKey("status")) {
			model.setStatus(jsonObject.getInteger("status"));
		}
		if (jsonObject.containsKey("createBy")) {
			model.setCreateBy(jsonObject.getString("createBy"));
		}
		if (jsonObject.containsKey("createTime")) {
			model.setCreateTime(jsonObject.getDate("createTime"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<SchedulerExecution> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (SchedulerExecution model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(SchedulerExecution model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getSchedulerId() != null) {
			jsonObject.put("schedulerId", model.getSchedulerId());
		}
		if (model.getBusinessKey() != null) {
			jsonObject.put("businessKey", model.getBusinessKey());
		}
		jsonObject.put("count", model.getCount());
		jsonObject.put("value", model.getValue());
		jsonObject.put("runYear", model.getRunYear());
		jsonObject.put("runMonth", model.getRunMonth());
		jsonObject.put("runWeek", model.getRunWeek());
		jsonObject.put("runQuarter", model.getRunQuarter());
		jsonObject.put("runDay", model.getRunDay());
		jsonObject.put("runTime", model.getRunTime());
		if (model.getJobNo() != null) {
			jsonObject.put("jobNo", model.getJobNo());
		}
		jsonObject.put("status", model.getStatus());
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime",
					DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(SchedulerExecution model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getSchedulerId() != null) {
			jsonObject.put("schedulerId", model.getSchedulerId());
		}
		if (model.getBusinessKey() != null) {
			jsonObject.put("businessKey", model.getBusinessKey());
		}
		jsonObject.put("count", model.getCount());
		jsonObject.put("value", model.getValue());
		jsonObject.put("runYear", model.getRunYear());
		jsonObject.put("runMonth", model.getRunMonth());
		jsonObject.put("runWeek", model.getRunWeek());
		jsonObject.put("runQuarter", model.getRunQuarter());
		jsonObject.put("runDay", model.getRunDay());
		jsonObject.put("runTime", model.getRunTime());
		if (model.getJobNo() != null) {
			jsonObject.put("jobNo", model.getJobNo());
		}
		jsonObject.put("status", model.getStatus());
		if (model.getCreateBy() != null) {
			jsonObject.put("createBy", model.getCreateBy());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime",
					DateUtils.getDateTime(model.getCreateTime()));
		}
		return jsonObject;
	}

	private SchedulerExecutionJsonFactory() {

	}

}
