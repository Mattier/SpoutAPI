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
package org.spout.api.command;

import org.spout.api.chat.ChatArguments;
import org.spout.api.lang.Locale;
import org.spout.api.permissions.PermissionsSubject;

public interface CommandSource extends PermissionsSubject{
	/**
	 * Sends a text message to the source of the command.
	 *
	 * @param message the message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendMessage(Object... message);

	/**
	 * Send a command to the other side (if server, send to client, and vice versa)
	 *
	 * @param command The command to send
	 * @param arguments The arguments to send with the command
	 */
	public void sendCommand(String command, ChatArguments arguments);

	/**
	 * Handle a command locally
	 *
	 * @param command The command to handle
	 * @param arguments The command's arguments
	 */
	public void processCommand(String command, ChatArguments arguments);

	/**
	 * Sends a text message to the source of the command.
	 *
	 * @param message the message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendMessage(ChatArguments message);

	/**
	 * Sends a message to the client without any processing by the server,
	 * except to prevent exploits.
	 *
	 * @param message The message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendRawMessage(Object... message);

	/**
	 * Sends a message to the client without any processing by the server,
	 * except to prevent exploits.
	 *
	 * @param message The message to send
	 * @return whether the message was sent correctly
	 */
	public boolean sendRawMessage(ChatArguments message);
	
	/**
	 * @return the preferred locale of the sender
	 */
	public Locale getPreferredLocale();
}
