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

package com.glaf.core.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.TreeModel;
import com.glaf.core.cache.CacheFactory;

import com.glaf.core.config.Environment;
import com.glaf.core.config.SystemConfig;
import com.glaf.core.context.ContextFactory;
import com.glaf.core.domain.Database;
import com.glaf.core.identity.Agent;
import com.glaf.core.identity.Group;
import com.glaf.core.identity.Role;
import com.glaf.core.identity.Tenant;
import com.glaf.core.identity.User;
import com.glaf.core.identity.util.TenantJsonFactory;
import com.glaf.core.identity.util.UserJsonFactory;
import com.glaf.core.query.GroupQuery;
import com.glaf.core.query.TreeModelQuery;
import com.glaf.core.query.UserQuery;
import com.glaf.core.service.EntityService;
import com.glaf.core.service.IDatabaseService;
import com.glaf.core.service.ITablePageService;
import com.glaf.core.util.Constants;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.hash.JenkinsHash;

public class IdentityFactory {
	protected final static Log logger = LogFactory.getLog(IdentityFactory.class);

	protected static volatile EntityService entityService;

	protected static volatile IDatabaseService databaseService;

	protected static volatile ITablePageService tablePageService;

	/**
	 * 获取委托人编号集合
	 * 
	 * @param assignTo
	 *            受托人编号
	 * @return
	 */
	public static List<String> getAgentIds(String assignTo) {
		List<String> agentIds = new java.util.ArrayList<String>();
		List<Object> list = getEntityService().getList("getAgents", assignTo);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
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

	/**
	 * 获取委托人对象集合
	 * 
	 * @param assignTo
	 *            受托人编号
	 * @return
	 */
	public static List<Agent> getAgents(String assignTo) {
		List<Agent> agents = new java.util.ArrayList<Agent>();
		List<Object> list = getEntityService().getList("getAgents", assignTo);
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof Agent) {
					Agent agent = (Agent) obj;
					if (!agent.isValid()) {
						continue;
					}
					switch (agent.getAgentType()) {
					case 0:// 全局代理
						agents.add(agent);
						break;
					default:
						break;
					}
				}
			}
		}
		return agents;
	}

	public static List<TreeModel> getChildrenTreeModels(long id) {
		List<Object> list = getEntityService().getList("getChildrenTreeModels", id);
		List<TreeModel> treeModels = new java.util.ArrayList<TreeModel>();
		if (list != null && !list.isEmpty()) {
			Iterator<Object> iter = list.iterator();
			while (iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof TreeModel) {
					TreeModel treeModel = (TreeModel) obj;
					List<TreeModel> children = getChildrenTreeModels(treeModel.getId());
					treeModel.setChildren(children);
					treeModels.add(treeModel);
				}
			}
		}
		return treeModels;
	}

	public static List<Database> getDatabases(String actorId) {
		return getDatabaseService().getDatabases(actorId);
	}

	public static IDatabaseService getDatabaseService() {
		if (databaseService == null) {
			databaseService = ContextFactory.getBean("databaseService");
		}
		return databaseService;
	}

	public static EntityService getEntityService() {
		if (entityService == null) {
			entityService = ContextFactory.getBean("entityService");
		}
		return entityService;
	}

	public static List<String> getGradeIds(String userId, String tenantId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("tenantId", tenantId);
		List<String> gradeIds = new ArrayList<String>();
		List<Map<String, Object>> datalist = getTablePageService().getListData(
				" select E.GRADEID_ as \"gradeId\" from HEALTH_GRADE_PRIVILEGE E where E.USERID_ = #{userId} and E.TENANTID_ = #{tenantId}  ",
				params);
		if (datalist != null && !datalist.isEmpty()) {
			for (Map<String, Object> dataMap : datalist) {
				gradeIds.add(ParamUtils.getString(dataMap, "gradeId"));
			}
		}
		return gradeIds;
	}

	/**
	 * 获取全部组Map
	 * 
	 * @return
	 */
	public static Map<String, Group> getGroupMap() {
		Map<String, Group> groupMap = new LinkedHashMap<String, Group>();
		List<Object> list = getEntityService().getList("selectGroups", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Group) {
					Group group = (Group) object;
					groupMap.put(group.getGroupId(), group);
				}
			}
		}
		return groupMap;
	}

	public static List<Group> getGroups() {
		List<Group> groups = new ArrayList<Group>();
		List<Object> list = getEntityService().getList("selectGroups", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Group) {
					Group group = (Group) object;
					groups.add(group);
				}
			}
		}
		return groups;
	}

	public static List<Group> getGroups(GroupQuery query) {
		List<Group> groups = new ArrayList<Group>();
		List<Object> list = getEntityService().getList("selectGroups", query);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Group) {
					Group group = (Group) object;
					groups.add(group);
				}
			}
		}
		return groups;
	}

	public static List<Group> getGroups(String tenantId) {
		GroupQuery query = new GroupQuery();
		query.tenantId(tenantId);
		List<Group> groups = new ArrayList<Group>();
		List<Object> list = getEntityService().getList("selectGroups", query);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Group) {
					Group group = (Group) object;
					groups.add(group);
				}
			}
		}
		return groups;
	}

	/**
	 * 获取登录用户信息
	 * 
	 * @param actorId
	 * @return
	 */
	public static LoginContext getLoginContext(String userId) {
		String currentName = Environment.getCurrentSystemName();
		if (!StringUtils.equals(currentName, Environment.DEFAULT_SYSTEM_NAME)) {
			return null;
		}
		String cacheKey = Constants.LOGIN_USER_CACHE + userId;
		if (SystemConfig.getBoolean("use_query_cache")) {
			String text = CacheFactory.getString("login_context", cacheKey);
			if (StringUtils.isNotEmpty(text)) {
				try {
					JSONObject jsonObject = JSON.parseObject(text);
					return LoginContextUtils.jsonToObject(jsonObject);
				} catch (Exception ex) {
				}
			}
		}
		User user = (User) getEntityService().getById("getUserById", userId);
		if (user != null) {
			LoginContext loginContext = new LoginContext(user);
			List<String> roles = new ArrayList<String>();
			/**
			 * 获取本人的角色权限
			 */
			List<String> list = getUserRoleCodes(userId);
			if (list != null && !list.isEmpty()) {
				roles.addAll(list);
			}
			logger.debug("user roles:" + roles);
			loginContext.setRoles(roles);

			if (StringUtils.isNotEmpty(user.getTenantId())) {
				Tenant tenant = (Tenant) getEntityService().getById("getTenantById", user.getTenantId());
				loginContext.setTenant(tenant);

				if (tenant != null) {
					long databaseId = loginContext.getTenant().getDatabaseId();
					if (databaseId > 0) {
						Database database = getDatabaseService().getDatabaseById(databaseId);
						if (database != null) {
							loginContext.setCurrentSystemName(database.getName());
						}
					}
				}

				List<String> gradeIds = getGradeIds(userId, user.getTenantId());
				loginContext.setGradeIds(gradeIds);
			}

			/**
			 * 获取本人 管理的租户集合
			 */
			List<String> managedTenantIds = getManagedTenantIds(userId);
			if (managedTenantIds != null && !managedTenantIds.isEmpty()) {
				loginContext.setManagedTenantIds(managedTenantIds);
			}

			if (SystemConfig.getBoolean("use_query_cache")) {
				CacheFactory.put("login_context", cacheKey, loginContext.toJsonObject().toJSONString());
			}
			return loginContext;
		}
		return null;
	}

	/**
	 * 获取委托人管理的租户集合
	 * 
	 * @param grantee
	 *            受托人编号
	 * @return
	 */
	public static List<String> getManagedTenantIds(String grantee) {
		List<String> managedTenantIds = new java.util.ArrayList<String>();
		List<Object> list = getEntityService().getList("getManagedTenantIds", grantee);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof String) {
					managedTenantIds.add(object.toString());
				}
			}
		}
		return managedTenantIds;
	}

	/**
	 * 获取全部部门Map
	 * 
	 * @return
	 */
	public static Map<Long, TreeModel> getOrganizationMap() {
		Map<Long, TreeModel> organizationMap = new LinkedHashMap<Long, TreeModel>();
		List<Object> list = getEntityService().getList("getOrganizations", null);
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof TreeModel) {
					TreeModel organization = (TreeModel) obj;
					organizationMap.put(Long.valueOf(organization.getId()), organization);
				}
			}
		}
		return organizationMap;
	}

	public static List<TreeModel> getOrganizations(String tenantId) {
		TreeModelQuery query = new TreeModelQuery();
		query.tenantId(tenantId);
		List<Object> list = getEntityService().getList("getOrganizations", query);
		List<TreeModel> treeModels = new ArrayList<TreeModel>();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof TreeModel) {
					TreeModel organization = (TreeModel) object;
					treeModels.add(organization);
				}
			}
		}
		return treeModels;
	}

	public static List<TreeModel> getOrganizations(TreeModelQuery query) {
		List<Object> list = getEntityService().getList("getOrganizations", query);
		List<TreeModel> treeModels = new ArrayList<TreeModel>();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof TreeModel) {
					TreeModel organization = (TreeModel) object;
					treeModels.add(organization);
				}
			}
		}
		return treeModels;
	}

	/**
	 * 获取全部角色Map
	 * 
	 * @return
	 */
	public static Map<String, Role> getRoleMap() {
		Map<String, Role> roleMap = new LinkedHashMap<String, Role>();
		List<Object> list = getEntityService().getList("getRoles", null);
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof Role) {
					Role role = (Role) obj;
					roleMap.put(role.getRoleId(), role);
				}
			}
		}
		return roleMap;
	}

	public static List<Role> getRoles() {
		List<Role> roles = new ArrayList<Role>();
		List<Object> list = getEntityService().getList("getRoles", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Role) {
					Role role = (Role) object;
					roles.add(role);
				}
			}
		}
		return roles;
	}

	public static ITablePageService getTablePageService() {
		if (tablePageService == null) {
			tablePageService = ContextFactory.getBean("tablePageService");
		}
		return tablePageService;
	}

	/**
	 * 根据租户编号获取租户对象
	 * 
	 * @param tenantId
	 * @return
	 */
	public static Tenant getTenantById(String tenantId) {
		String cacheKey = "cache_sys_tenant_" + tenantId;
		String text = CacheFactory.getString("tenant", cacheKey);
		if (text != null) {
			try {
				JSONObject jsonObject = JSON.parseObject(text);
				return TenantJsonFactory.jsonToObject(jsonObject);
			} catch (Exception ex) {
			}
		}
		Tenant tenant = (Tenant) getEntityService().getById("getTenantById", tenantId);
		if (tenant != null) {
			CacheFactory.put("tenant", cacheKey, tenant.toJsonObject().toJSONString());
		}
		return tenant;
	}

	/**
	 * 通过租户编号获取租户Hash码
	 * 
	 * @param tenantId
	 * @return
	 */
	public static int getTenantHash(String tenantId) {
		return Math.abs(JenkinsHash.getInstance().hash(tenantId.getBytes()))
				% com.glaf.core.util.Constants.TABLE_PARTITION;
	}

	public static List<Tenant> getTenants() {
		List<Tenant> tenants = new ArrayList<Tenant>();
		List<Object> list = getEntityService().getList("getTenants", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof Tenant) {
					Tenant tenant = (Tenant) object;
					tenants.add(tenant);
				}
			}
		}
		return tenants;
	}

	public static TreeModel getTopOrganization() {
		TreeModel treeModel = getTreeModelByCode("012"); // 部门结构编码
		if (treeModel != null) {
			TreeModelQuery query = new TreeModelQuery();
			query.parentId(treeModel.getId());
			List<Object> list = getEntityService().getList("getOrganizations", query);
			List<TreeModel> treeModels = new ArrayList<TreeModel>();
			if (list != null && !list.isEmpty()) {
				for (Object object : list) {
					if (object instanceof TreeModel) {
						TreeModel organization = (TreeModel) object;
						treeModels.add(organization);
					}
				}
			}
			if (treeModels.size() == 1) {
				treeModel = treeModels.get(0);// 取部门根结构
			}
		}

		return treeModel;
	}

	public static TreeModel getTreeModelByCode(String code) {
		return (TreeModel) getEntityService().getById("getTreeModelByCode", code);
	}

	public static TreeModel getTreeModelById(Long id) {
		return (TreeModel) getEntityService().getById("getTreeModelById", id);
	}

	/**
	 * 根据用户名获取用户对象
	 * 
	 * @param actorId
	 * @return
	 */
	public static User getUser(String actorId) {
		String cacheKey = "cache_sys_user_" + actorId;
		String text = CacheFactory.getString("user", cacheKey);
		if (text != null) {
			try {
				JSONObject jsonObject = JSON.parseObject(text);
				return UserJsonFactory.jsonToObject(jsonObject);
			} catch (Exception ex) {
			}
		}
		User user = (User) getEntityService().getById("getUserById", actorId);
		if (user != null) {
			CacheFactory.put("user", cacheKey, user.toJsonObject().toJSONString());
		}
		return user;
	}

	/**
	 * 根据用户ID获取用户对象
	 * 
	 * @param userId
	 * @return
	 */
	public static User getUserByUserId(Long userId) {
		User user = (User) getEntityService().getById("getUserByUserId", userId);
		return user;
	}

	/**
	 * 获取全部用户Map
	 * 
	 * @return
	 */
	public static Map<String, User> getUserMap() {
		Map<String, User> userMap = new LinkedHashMap<String, User>();
		List<Object> list = getEntityService().getList("getUsers", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User user = (User) object;
					userMap.put(user.getActorId(), user);
				}
			}
		}
		return userMap;
	}

	/**
	 * 获取全部用户Map
	 * 
	 * @return
	 */
	public static Map<String, User> getUserMap(Map<String, Object> params) {
		Map<String, User> userMap = new LinkedHashMap<String, User>();
		List<Object> list = getEntityService().getList("getUsers", params);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User user = (User) object;
					userMap.put(user.getActorId(), user);
				}
			}
		}
		return userMap;
	}

	/**
	 * 获取全部用户Map
	 * 
	 * @return
	 */
	public static Map<String, User> getUserMap(String tenantId) {
		UserQuery query = new UserQuery();
		query.tenantId(tenantId);
		Map<String, User> userMap = new LinkedHashMap<String, User>();
		List<Object> list = getEntityService().getList("getUsers", query);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User user = (User) object;
					userMap.put(user.getActorId(), user);
				}
			}
		}
		return userMap;
	}

	public static List<String> getUserRoleCodes(String actorId) {
		UserQuery query = new UserQuery();
		query.actorId(actorId);
		String statementId = "getUserRoleCodes";

		List<Object> list = getEntityService().getList(statementId, query);
		List<String> roles = new ArrayList<String>();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				roles.add(object.toString());
			}
		}
		return roles;
	}

	public static List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		List<Object> list = getEntityService().getList("getUsers", null);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User u = (User) object;
					users.add(u);
				}
			}
		}
		return users;
	}

	public static List<User> getUsers(String tenantId) {
		UserQuery query = new UserQuery();
		query.tenantId(tenantId);
		List<User> users = new ArrayList<User>();
		List<Object> list = getEntityService().getList("getUsers", query);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User u = (User) object;
					users.add(u);
				}
			}
		}
		return users;
	}

	public static List<User> getUsers(UserQuery query) {
		List<User> users = new ArrayList<User>();
		List<Object> list = getEntityService().getList("getUsers", query);
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				if (object instanceof User) {
					User u = (User) object;
					users.add(u);
				}
			}
		}
		return users;
	}

	public static List<User> searchUsers(String searchWord) {
		List<User> users = new ArrayList<User>();
		List<Object> list = getEntityService().getList("searchUsers", searchWord);
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof User) {
					User u = (User) obj;
					users.add(u);
				}
			}
		}
		return users;
	}

	public static void setDatabaseService(IDatabaseService databaseService) {
		IdentityFactory.databaseService = databaseService;
	}

	public static void setEntityService(EntityService entityService) {
		IdentityFactory.entityService = entityService;
	}

	private IdentityFactory() {

	}

}