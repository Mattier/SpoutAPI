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
package org.spout.api.signal;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SignalTest {
	public boolean gotSignal = false;
	@Test
	public void testSignalWithStringMethod() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();

		Receiver receiver = new Receiver();

		emittingObject.subscribe("test", receiver, "onTest");

		emittingObject.doSomething("hello");

		if (!gotSignal) {
			fail("Did not get a signal");
		}

		gotSignal = false;
	}

	@Test
	public void testSignalWithoutArguments() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();

		Receiver receiver = new Receiver();

		emittingObject.subscribe("clicked", receiver, "onClick");

		emittingObject.click();

		if (!gotSignal) {
			fail("Did not get a signal");
		}

		gotSignal = false;
	}

	@Test
	public void testSignalUnsubscribing() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();

		Receiver receiver = new Receiver();

		emittingObject.subscribe("clicked", receiver, "onClick");
		emittingObject.unsubscribe("clicked", receiver);

		emittingObject.click();

		if (gotSignal) {
			fail("Unsubscribed, but still got a signal");
		}

		gotSignal = false;
	}

	@Test
	public void testSignalUnsubscribingAll() throws SecurityException, NoSuchMethodException {
		SignalTestClass emittingObject = new SignalTestClass();

		Receiver receiver = new Receiver();

		emittingObject.subscribe("clicked", receiver, "onClick");
		emittingObject.unsubscribe(receiver);

		emittingObject.click();

		if (gotSignal) {
			fail("Unsubscribed, but still got a signal");
		}

		gotSignal = false;
	}

	public class Receiver {
		public void onTest(String arg1) {
			gotSignal = true;
		}

		public void onClick() {
			gotSignal = true;
		}
	}
	
	@Test
	public void testSignalUnsubscribingWhileIterating() throws SecurityException, NoSuchMethodException {
		gotSignal = false;
		final SignalTestClass emittingObject = new SignalTestClass();

		Object receiver = new Object() {
			@SuppressWarnings("unused")
			public void doAction() {
				emittingObject.unsubscribe(this);
				gotSignal = true;
			}
		};
		
		emittingObject.subscribe("clicked", receiver, "doAction");
		
		emittingObject.click();
		
		assertTrue(gotSignal);
		
		gotSignal = false;
		
		emittingObject.click();
		
		assertFalse(gotSignal);
		
		gotSignal = false;
	}

	public class SignalTestClass extends SignalObject {
		{
			registerSignal(new Signal("test", String.class));
			registerSignal(new Signal("clicked"));
		}

		public void doSomething(String arg) {
			emit("test", arg);
		}

		public void click() {
			emit("clicked");
		}
	}
}
