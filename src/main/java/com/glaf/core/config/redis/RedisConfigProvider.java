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
package com.glaf.core.config.redis;

import java.util.Properties;

import com.glaf.core.config.Config;
import com.glaf.core.config.ConfigProvider;
import com.glaf.core.config.redis.RedisConfig;

/**
 * Redis 分布式配置实现
 */
public class RedisConfigProvider implements ConfigProvider {

	@Override
	public Config buildConfig(String regionName, boolean autoCreate) {
		return new RedisConfig(regionName);
	}

	@Override
	public String name() {
		return "redis";
	}

	@Override
	public void start(Properties props) {

	}

	@Override
	public void stop() {

	}
}