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

package com.glaf.base.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.glaf.core.cache.CacheFactory;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.identity.Agent;
import com.glaf.core.identity.User;
import com.glaf.core.service.EntityService;

import com.glaf.base.modules.sys.model.SysRole;
import com.glaf.base.modules.sys.model.SysUser;
import com.glaf.base.modules.sys.service.SysApplicationService;
import com.glaf.base.modules.sys.service.SysRoleService;
import com.glaf.base.modules.sys.service.SysTreeService;
import com.glaf.base.modules.sys.service.SysUserService;
import com.glaf.base.modules.sys.util.SysUserJsonFactory;
import com.glaf.base.utils.ContextUtil;

public class BaseIdentityFactory {

	protected static final Log logger = LogFactory.getLog(BaseIdentityFactory.class);

	protected static volatile EntityService entityService;

	protected static volatile SysApplicationService sysApplicationService;

	protected static volatile SysRoleService sysRoleService;

	protected static volatile SysTreeService sysTreeService;

	protected static volatile SysUserService sysUserService;

	/**
	 * 获取委托人编号集合（用户登录账号的集合）
	 * 
	 * @param assignTo
	 *            受托人编号（登录账号）
	 * @return
	 */
	public static List<String> getAgentIds(String assignTo) {
		List<String> agentIds = new ArrayList<String>();
		List<Object> rows = getEntityService().getList("getAgents", assignTo);
		if (rows != null && !rows.isEmpty()) {
			for (Object object : rows) {
				if (object instanceof Agent) {
					Agent agent = (Agent) object;
					if (!agent.isValid()) {
						continue;
					}
					switch (agent.getAgentType()) {
					case 0:// 全局代理
						agentIds.add(agent.getAssignFrom());
						break;
					default:
						break;
					}
				}
			}
		}
		return agentIds;
	}

	public static EntityService getEntityService() {
		if (entityService == null) {
			entityService = ContextFactory.getBean("entityService");
		}
		return entityService;
	}

	/**
	 * 获取全部用户
	 * 
	 * @return
	 */
	public static Map<String, SysUser> getLowerCaseUserMap() {
		Map<String, SysUser> userMap = new LinkedHashMap<String, SysUser>();
		List<SysUser> users = getSysUserService().getSysUserList(0);
		if (users != null && !users.isEmpty()) {
			for (SysUser user : users) {
				userMap.put(user.getUserId().toLowerCase(), user);
			}
		}
		return userMap;
	}

	/**
	 * 通过角色代码获取角色
	 * 
	 * @param code
	 *            角色代码
	 * @return
	 */
	public static SysRole getRoleByCode(String code) {
		return getSysRoleService().findByCode(code);
	}

	/**
	 * 通过角色编号获取角色
	 * 
	 * @param id
	 *            角色ID
	 * @return
	 */
	public static SysRole getRoleById(String id) {
		return getSysRoleService().findById(id);
	}

	/**
	 * 获取全部角色 Map
	 * 
	 * @return
	 */
	public static Map<String, SysRole> getRoleMap() {
		Map<String, SysRole> roleMap = new HashMap<String, SysRole>();
		List<SysRole> roles = getSysRoleService().getSysRoleList();
		if (roles != null && !roles.isEmpty()) {
			for (SysRole role : roles) {
				roleMap.put(role.getCode(), role);
			}
		}
		return roleMap;
	}

	/**
	 * 获取全部角色
	 * 
	 * @return
	 */
	public static List<SysRole> getRoles() {
		List<SysRole> roles = getSysRoleService().getSysRoleList();
		return roles;
	}

	public static SysApplicationService getSysApplicationService() {
		if (sysApplicationService == null) {
			sysApplicationService = ContextFactory.getBean("sysApplicationService");
		}
		return sysApplicationService;
	}

	public static SysRoleService getSysRoleService() {
		if (sysRoleService == null) {
			sysRoleService = ContextFactory.getBean("sysRoleService");
		}
		return sysRoleService;
	}

	public static SysTreeService getSysTreeService() {
		if (sysTreeService == null) {
			sysTreeService = ContextFactory.getBean("sysTreeService");
		}

		return sysTreeService;
	}

	/**
	 * 根据用户名获取用户对象
	 * 
	 * @param actorId
	 *            用户登录账号
	 * @return
	 */
	public static SysUser getSysUser(String actorId) {
		return getSysUserService().findByAccountWithAll(actorId);
	}

	public static SysUserService getSysUserService() {
		if (sysUserService == null) {
			sysUserService = ContextFactory.getBean("sysUserService");
		}
		return sysUserService;
	}

	/**
	 * 根据用户名获取用户对象
	 * 
	 * @param actorId
	 *            用户登录账号
	 * @return
	 */
	public static SysUser getSysUserWithAll(String actorId) {
		SysUser user = getSysUserService().findByAccountWithAll(actorId);
		if (user != null) {
			ContextUtil.put(actorId, user);
		}
		return user;
	}

	/**
	 * 根据用户名获取用户对象
	 * 
	 * @param actorId
	 *            用户登录账号
	 * @return
	 */
	public static User getUser(String actorId) {
		String cacheKey = "cache_SYS_USER_" + actorId;
		String text = CacheFactory.getString("user", cacheKey);
		if (text != null) {
			try {
				JSONObject jsonObject = JSON.parseObject(text);
				return SysUserJsonFactory.jsonToObject(jsonObject);
			} catch (Exception ex) {
			}
		}
		SysUser user = getSysUserService().findByAccount(actorId);
		if (user != null) {
			CacheFactory.put("user", cacheKey, user.toJsonObject().toJSONString());
		}
		return user;
	}

	/**
	 * 获取全部用户Map
	 * 
	 * @return
	 */
	public static Map<String, SysUser> getUserMap() {
		Map<String, SysUser> userMap = new LinkedHashMap<String, SysUser>();
		List<SysUser> users = getSysUserService().getSysUserList(0);
		if (users != null && !users.isEmpty()) {
			for (SysUser user : users) {
				userMap.put(user.getUserId(), user);
			}
		}
		return userMap;
	}

	/**
	 * 获取某些用户的角色代码
	 * 
	 * @param actorIds
	 * @return
	 */
	public static List<String> getUserRoleCodes(List<String> actorIds) {
		List<String> codes = new ArrayList<String>();
		List<SysRole> list = getUserRoles(actorIds);
		if (list != null && !list.isEmpty()) {
			for (SysRole role : list) {
				if (!codes.contains(role.getCode())) {
					codes.add(role.getCode());
				}
			}
		}
		return codes;
	}

	public static List<SysRole> getUserRoles(List<String> actorIds) {
		return getSysUserService().getUserRoles(actorIds);
	}

	/**
	 * 获取某个用户及代理人的角色编号
	 * 
	 * @param actorId
	 *            用户登录账号
	 * @return
	 */
	public static List<String> getUserRoles(String actorId) {
		List<String> actorIds = new ArrayList<String>();
		actorIds.add(actorId);
		return getUserRoleCodes(actorIds);
	}

	/**
	 * 获取全部用户
	 * 
	 * @return
	 */
	public static List<SysUser> getUsers() {
		return getSysUserService().getSysUserList(0);
	}

	public static void setSysApplicationService(SysApplicationService sysApplicationService) {
		BaseIdentityFactory.sysApplicationService = sysApplicationService;
	}

	public static void setSysRoleService(SysRoleService sysRoleService) {
		BaseIdentityFactory.sysRoleService = sysRoleService;
	}

	public static void setSysTreeService(SysTreeService sysTreeService) {
		BaseIdentityFactory.sysTreeService = sysTreeService;
	}

	public static void setSysUserService(SysUserService sysUserService) {
		BaseIdentityFactory.sysUserService = sysUserService;
	}

}