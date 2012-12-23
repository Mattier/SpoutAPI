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
package org.spout.api.inventory;

/**
 * Represents an inventory that is held by something.
 *
 * @param <T> the type of object holding this inventory.
 */
public class HeldInventory<T> extends Inventory {
	/**
	 * The serial version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The holder of this inventory.
	 */
	private transient T holder = null;
	
	/**
	 * Constructs a new held inventory with an initial capacity.
	 *
	 * @param holder
	 * @param size the initial capacity
	 */
	public HeldInventory(int size) {
		super(size);
	}

	/**
	 * Constructs a new held Inventory with an initial slot to {@link ItemStack} 
	 * mapping
	 *
	 * @param holder
	 * @param contents array of the contents of the inventory
	 */
	public HeldInventory(ItemStack... contents) {
		super(contents);
	}
	
	/**
	 * Sets the holder of this inventory.
	 *
	 * @param holder
	 */
	public void setHolder(T holder) {
		this.holder = holder;
	}
	
	/**
	 * Returns the holder of this inventory.
	 *
	 * @return holder
	 */
	public T getHolder() {
		return holder;
	}
}
