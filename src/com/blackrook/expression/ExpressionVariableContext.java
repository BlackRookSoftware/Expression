/*******************************************************************************
 * Copyright (c) 2017-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.expression;

import java.util.Comparator;

import com.blackrook.commons.Sizable;
import com.blackrook.commons.util.ArrayUtils;

/**
 * An open variable set in which values can be set.
 * The internals are written so that the storage uses few memory allocations/deletions. 
 * @author Matthew Tropiano
 */
public class ExpressionVariableContext implements ExpressionVariableSet, Sizable
{
	/** Default capacity. */
	public static final int DEFAULT_CAPACITY = 4;

	private static final Comparator<Entry> ENTRY_COMPARATOR = new Comparator<Entry>()
	{
		@Override
		public int compare(Entry e1, Entry e2)
		{
			return e1.name.compareTo(e2.name);
		}
		
	};
	
	/** List of entries. */
	private Entry[] entries;
	/** Count. */
	private int entryCount;

	/**
	 * Creates a context with a default size.
	 * @see #DEFAULT_CAPACITY
	 */
	public ExpressionVariableContext()
	{
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Creates a context with a default size.
	 * @param capacity the initial capacity.
	 */
	public ExpressionVariableContext(int capacity)
	{
		if (capacity < 1) 
			capacity = 1;
		expand(capacity);
		this.entryCount = 0;
	}
	
	// Expands this.
	private void expand(int capacity)
	{
		Entry[] newEntries = new Entry[capacity];
		if (entries != null)
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
		for (int i = entries != null ? entries.length : 0; i < newEntries.length; i++)
			newEntries[i] = new Entry();
		entries = newEntries;
	}
	
	/**
	 * Clears the context.
	 */
	public void clear()
	{
		this.entryCount = 0;
	}
	
	@Override
	public ExpressionValue get(String name)
	{
		int u = entryCount, l = 0;
		int i = (u+l)/2;
		int prev = u;
		
		while (i != prev)
		{
			if (entries[i].name.equals(name))
				return entries[i].value;

			int c = entries[i].name.compareTo(name); 
			
			if (c < 0)
				l = i;
			else if (c == 0)
				return entries[i].value;
			else
				u = i;
			
			prev = i;
			i = (u+l)/2;
		}
		
		return null;
	}

	/**
	 * Gets a corresponding expression value by name.
	 * @param name the name of the variable.
	 * @param value the value to set.
	 */
	public void set(String name, boolean value)
	{
		ExpressionValue ev = get(name); 
		if (ev != null)
		{
			ev.set(value);
			return;
		}
		
		if (entryCount == entries.length - 1)
			expand(entries.length * 2);
		entries[entryCount].name = name;
		entries[entryCount].value.set(value);
		ArrayUtils.sortFrom(entries, entryCount, ENTRY_COMPARATOR);
		entryCount++;
	}

	/**
	 * Gets a corresponding expression value by name.
	 * @param name the name of the variable.
	 * @param value the value to set.
	 */
	public void set(String name, long value)
	{
		ExpressionValue ev = get(name); 
		if (ev != null)
		{
			ev.set(value);
			return;
		}
		
		if (entryCount == entries.length - 1)
			expand(entries.length * 2);
		entries[entryCount].name = name;
		entries[entryCount].value.set(value);
		ArrayUtils.sortFrom(entries, entryCount, ENTRY_COMPARATOR);
		entryCount++;
	}

	/**
	 * Gets a corresponding expression value by name.
	 * @param name the name of the variable.
	 * @param value the value to set.
	 */
	public void set(String name, double value)
	{
		ExpressionValue ev = get(name); 
		if (ev != null)
		{
			ev.set(value);
			return;
		}
		
		if (entryCount == entries.length - 1)
			expand(entries.length * 2);
		entries[entryCount].name = name;
		entries[entryCount].value.set(value);
		ArrayUtils.sortFrom(entries, entryCount, ENTRY_COMPARATOR);
		entryCount++;
	}

	/**
	 * Gets a corresponding expression value by name.
	 * @param name the name of the variable.
	 * @param value the value to set.
	 */
	public void set(String name, ExpressionValue value)
	{
		ExpressionValue ev = get(name); 
		if (ev != null)
		{
			ev.set(value);
			return;
		}
		
		if (entryCount == entries.length - 1)
			expand(entries.length * 2);
		entries[entryCount].name = name;
		entries[entryCount].value.set(value);
		ArrayUtils.sortFrom(entries, entryCount, ENTRY_COMPARATOR);
		entryCount++;
	}

	@Override
	public int size()
	{
		return entryCount;
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int i = 0;
		while (i < entryCount)
		{
			sb.append(entries[i]);
			if (i < entryCount - 1)
				sb.append(", ");
			i++;
		}
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * A single entry.
	 */
	private static class Entry
	{
		private String name;
		private ExpressionValue value;
		
		Entry()
		{
			this.name = null;
			this.value = ExpressionValue.create(false);
		}
		
		@Override
		public String toString()
		{
			return name + ": " + value;
		}
		
	}
	
}
