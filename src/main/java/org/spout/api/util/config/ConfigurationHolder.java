/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.util.config;

import org.apache.commons.lang3.ArrayUtils;
import org.spout.api.data.ValueHolderBase;

import java.util.Map;
import java.util.Set;

/**
 * This object holds a reference to a ConfigurationNode and provides all the methods to
 * get its value, but using the default provided in the constructor
 */
public class ConfigurationHolder extends ValueHolderBase implements ConfigurationNodeSource {
	private Configuration configuration;
	private final String[] path;
	private Object def;

	public ConfigurationHolder(Configuration config, Object def, String... path) {
		this.path = path;
		this.configuration = config;
		this.def = def;
	}

	public ConfigurationHolder(Object value, String... path) {
		this(null, value, path);
	}

	private ConfigurationNode getNode() {
		if (getConfiguration() == null) {
			throw new IllegalStateException("The ConfigurationHolder at path " + ArrayUtils.toString(path) + " is not attached to a Configuration!");
		}
		return getConfiguration().getNode(getPathElements());
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration config) {
		this.configuration = config;
	}

	@Override
	public String[] getPathElements() {
		return path;
	}

	/**
	 * Gets the default value of this Configuration Holder
	 * @return the default value
	 */
	public Object getDefaultValue() {
		return this.def;
	}

	/**
	 * Sets the default value for this Configuration Holder
	 * @param def value to set to
	 */
	public void setDefaultValue(Object def) {
		this.def = def;
	}

	@Override
	public Object getValue() {
		return getNode().getValue(this.def);
	}

	@Override
	public Object getValue(Object def) {
		return getNode().getValue(this.def);
	}

	public Object setValue(Object value) {
		return getNode().setValue(value);
	}

	@Override
	public ConfigurationNode getChild(String name) {
		return getNode().getChild(name);
	}

	@Override
	public ConfigurationNode getChild(String name, boolean add) {
		return getNode().getChild(name, add);
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		return getNode().addChild(node);
	}

	@Override
	public void addChildren(ConfigurationNode... nodes) {
		getNode().addChildren(nodes);
	}

	@Override
	public ConfigurationNode removeChild(String key) {
		return getNode().removeChild(key);
	}

	@Override
	public ConfigurationNode removeChild(ConfigurationNode node) {
		return getNode().removeChild(node);
	}

	@Override
	public Map<String, ConfigurationNode> getChildren() {
		return getNode().getChildren();
	}

	@Override
	public Map<String, Object> getValues() {
		return getNode().getValues();
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return getNode().getKeys(deep);
	}

	@Override
	public ConfigurationNode getNode(String path) {
		return getNode().getNode(path);
	}

	@Override
	public ConfigurationNode getNode(String... path) {
		return getNode().getNode(path);
	}

	@Override
	public boolean hasChildren() {
		return getNode().hasChildren();
	}

	public boolean hasChild(String key) {
		return getNode().hasChild(key);
	}

	public boolean hasNode(String... path) {
		return getNode().hasNode(path);
	}
}
