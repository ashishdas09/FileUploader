/*******************************************************************************
 * WeakReferenceList.java
 * WeakReferenceList
 * <p>
 * Author(s): Ashish Das
 ******************************************************************************/

package com.ashishdas.fileuploader.internal.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class WeakReferenceList<T> implements Iterable<T>
{
	private ArrayList<WeakReference<T>> mList = new ArrayList<WeakReference<T>>();

	public void add(T item)
	{
		mList.add(new WeakReference<T>(item));
	}

	public int indexOf(T item)
	{
		final int count = mList.size();

		for (int i = 0; i < count; i++)
		{
			T weakItem = mList.get(i).get();

			if (weakItem == null)
			{
				continue;
			}

			if (weakItem.equals(item))
			{
				return i;
			}
		}

		return -1;
	}

	public boolean has(T item)
	{
		return indexOf(item) != -1;
	}

	public void remove(int index)
	{
		if (index >= 0 && index < mList.size())
		{
			mList.remove(index);
		}
	}

	public void remove(T item)
	{
		final int index = indexOf(item);

		if (index != -1)
		{
			mList.remove(index);
		}
	}

	public int size()
	{
		return mList.size();
	}

	public T get(int index)
	{
		if (index < 0 || index >= mList.size())
		{
			return null;
		}

		return mList.get(index).get();
	}

	@Override
	public Iterator<T> iterator()
	{
		return new WeakListIterator<T>(this);
	}

	protected static class WeakListIterator<T> implements Iterator<T>
	{
		private WeakReferenceList<T> mList;

		private int mNextIndex = 0;
		private boolean mCanRemove = false;

		public WeakListIterator(WeakReferenceList<T> list)
		{
			mList = list;
		}

		@Override
		public boolean hasNext()
		{
			return mNextIndex < mList.size();
		}

		@Override
		public T next()
		{
			//Check index is within bounds
			if (mNextIndex >= mList.size())
			{
				throw new NoSuchElementException();
			}

			//Retrieve next element
			T element = mList.get(mNextIndex);

			mCanRemove = true;    //Flag removable
			mNextIndex++;    //Move index onto next element

			return element;
		}

		@Override
		public void remove()
		{
			//Check removable support
			if (!mCanRemove)
			{
				throw new IllegalStateException();
			}

			//Remove only allowed once per next()
			mCanRemove = false;

			//Remove it
			mList.remove(mNextIndex);

			//Decrement next index to account for removal
			mNextIndex--;
		}

	}
}
