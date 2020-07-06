/*==================================================================================================
 RoboLib - An Expansion and Improvement Library for WPILibJ
 Copyright (C) 2015  Glossawy

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 =================================================================================================*/


package org.usfirst.frc.team1554.lib.collect;

import static org.usfirst.frc.team1554.lib.util.ReflectionHelper.newArray;

import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.util.Preconditions;
import org.usfirst.frc.team1554.lib.util.Predicates;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A flexible representation of an arbitrary length Array, attempt at a better ArrayList, with the sacrifice of losing
 * the strength of the Java Collections framework. This Array is either Ordered or Unordered and is Resizable. If
 * unordered then a memory copy is avoided when removing an element, the last element is moved to the now empty position
 * which is only okay when unordered. When unordered {@link #get(int)} should be avoided!
 *
 * @param <T>
 */
public class Array<T> implements Iterable<T> {

    /**
     * Returns an Ordered Empty Array with the provided element type. This Class object may be used later to
     * safely return an Array of that type.
     *
     * @param arrayType Class Reference of Type
     * @param <T>       Element Type
     * @return Empty Array of Type T
     */
    public static <T> Array<T> of(Class<T> arrayType) {
        return new Array<>(arrayType);
    }

    /**
     * Returns an ordered or unordered array of an explicit initial capacity and of the type provided. This class
     * object may be used later to get a normal array of that type, avoiding an unsafe cast.
     *
     * @param ordered   Whether or not element order should be maintained
     * @param capacity  Initial Array Size
     * @param arrayType Class Reference of Type
     * @param <T>       Element Type
     * @return Empty Array of Type T
     */
    public static <T> Array<T> of(boolean ordered, int capacity, Class<T> arrayType) {
        return new Array<>(ordered, capacity, arrayType);
    }

    /**
     * Returns an Ordered Array consisting of the provided elements.
     *
     * @param array Elements to initialize with
     * @param <T>   Element Type
     * @return Ordered Array with initial elements
     */
    @SafeVarargs
    public static <T> Array<T> with(T... array) {
        return new Array<>(array);
    }

    public T[] items;

    private int size;
    private boolean ordered;

    private Iterable<T> iterable;

    /**
     * Create an empty ordered array.
     */
    public Array() {
        this(true, 16);
    }

    /**
     * Create an empty ordered array of the the given type.
     *
     * @param arrayType Type Reference
     */
    public Array(Class<? extends T> arrayType) {
        this(true, 16, arrayType);
    }

    /**
     * Create an empty, ordered or unordered array.
     *
     * @param ordered  Whether or not order should be maintained
     * @param capacity Initial size of array
     */
    @SuppressWarnings("unchecked")
    public Array(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = (T[]) new Object[capacity];
    }

    /**
     * Creates an empty, ordered or unordered array of the given type.
     *
     * @param ordered   Whether or not order should be maintained
     * @param capacity  Initial size of array
     * @param arrayType Array Type Reference
     */
    @SuppressWarnings("unchecked")
    public Array(boolean ordered, int capacity, @SuppressWarnings("rawtypes") Class arrayType) {
        this.ordered = ordered;
        this.items = (T[]) newArray(arrayType, capacity);
    }

    /**
     * Creates a copy of the given Array
     *
     * @param array Array to Copy
     */
    public Array(Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        this.size = array.size;
        System.arraycopy(array.items, 0, this.items, 0, this.size);
    }

    /**
     * Wraps the given array in an Array object.
     *
     * @param array Array to wrap
     */
    public Array(T[] array) {
        this(true, array, 0, array.length);
    }

    /**
     * Wraps an array or sub-array in an Array object, ordered or unordered.
     *
     * @param ordered Whether or not order should be maintained
     * @param arr     Array to wrap
     * @param start   Start Index
     * @param count   Size to Copy
     */
    public Array(boolean ordered, T[] arr, int start, int count) {
        this(ordered, count, arr.getClass().getComponentType());
        this.size = count;

        Preconditions.checkElementIndex(start, arr.length);
        Preconditions.checkElementIndex(start + count, arr.length);
        System.arraycopy(arr, start, this.items, 0, this.size);
    }

    /**
     * Append a given value to the Array
     *
     * @param value Value to Append
     */
    public void add(T value) {

        // Resize if no room available, use a scaling factor of 1.75
        if (this.size == this.items.length) {
            resize(Math.max(8, (int) (this.size * 1.75f)));
        }

        this.items[this.size++] = value;
    }

    /**
     * Append entire Array to this Array
     *
     * @param array Array of Values to Append
     */
    public void addAll(Array<? extends T> array) {
        addAll(array, 0, array.size);
    }

    /**
     * Append an entire Array or subsequence of the Array to this Array, starting at 'start' and copying 'count' objects.
     *
     * @param array Array to Copy From
     * @param start Start Index
     * @param count Number of Elements to Copy
     */
    public void addAll(Array<? extends T> array, int start, int count) {
        Preconditions.checkElementIndex(start, array.size);
        Preconditions.checkElementIndex(start + count, array.size, "start + count MUST be <= array.size! Size: " + array.size);

        addAll(array.items, start, count);
    }

    /**
     * Append an entire array of objects to this Array
     *
     * @param items Array to append
     */
    @SuppressWarnings("unchecked")
    public void addAll(T... items) {
        addAll(items, 0, items.length);
    }

    /**
     * Append an entire array or sub-array to this Array
     *
     * @param array Array to append
     * @param start Start Index
     * @param count Number of Elements to Copy
     */
    public void addAll(T[] array, int start, int count) {
        final int requirement = this.size + count;
        if (requirement > this.items.length) {
            resize(Math.max(8, (int) (requirement * 1.75f)));
        }

        System.arraycopy(array, start, this.items, this.size, count);
        this.size += count;
    }

    /**
     * Get an element at the given index. This is only defined when the Array is ordered. Unordered Arrays may have
     * unexpected behavior.
     *
     * @param index Index of element
     * @return Element at Index
     */
    public T get(int index) {
        Preconditions.checkElementIndex(index, this.size);

        return this.items[index];
    }

    /**
     * Puts a value at the given index.
     *
     * @param index Index to Put
     * @param value Element to Put
     */
    public void set(int index, T value) {
        Preconditions.checkElementIndex(index, this.size);

        this.items[index] = value;
    }

    /**
     * Inserts a value into the array at the given Index
     *
     * @param index Index to insert at
     * @param value Element to insert
     */
    public void insert(int index, T value) {
        Preconditions.checkElementIndex(index, this.size + 1);

        if (this.size == this.items.length) {
            resize(Math.max(8, (int) (this.size * 1.75f)));
        }

        if (this.ordered) {
            System.arraycopy(this.items, index, this.items, index + 1, this.size - index);
        } else {
            this.items[this.size] = this.items[index];
        }

        this.size++;
        this.items[index] = value;
    }

    /**
     * Swap two elements given the indices
     *
     * @param first  First Index
     * @param second Second Index
     */
    public void swap(int first, int second) {
        Preconditions.checkElementIndex(first, this.size);
        Preconditions.checkElementIndex(second, this.size);

        final T fVal = this.items[first];
        this.items[first] = this.items[second];
        this.items[second] = fVal;
    }

    /**
     * Check if an element is contained in this Array using either {@link Object#equals(Object)} or an identity.
     *
     * @param val                Value to check for
     * @param identityComparison Whether or not '==' should be used in comparisons
     * @return True or False
     */
    public boolean contains(@Nullable T val, boolean identityComparison) {
        if (identityComparison || (val == null)) {
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i] == val) return true;
        } else {
            for (int i = 0; i < this.items.length; i++)
                if (this.items[i].equals(val)) return true;
        }

        return false;
    }

    /**
     * Obtains the index of an element in the Array. If it does not exist in the array, -1 is returned (akin to Collections)
     *
     * @param value              Value to find
     * @param identityComparison Whether or not '==' should be used in comparisons
     * @return Index of Value in Array
     */
    public int indexOf(@Nullable T value, boolean identityComparison) {
        if (identityComparison || (value == null)) {
            for (int i = 0; i < this.size; i++)
                if (this.items[i] == value) return i;
        } else {
            for (int i = 0; i < this.size; i++)
                if (this.items[i].equals(value)) return i;
        }

        return -1;
    }

    /**
     * Find the last index of a particular value in the array.
     *
     * @param value              Value to Locate
     * @param identityComparison Whether or not '==' should be used in comparisons
     * @return The last index at which the value can be found. Or -1.
     */
    public int lastIndexOf(@Nullable T value, boolean identityComparison) {
        if (identityComparison || (value == null)) {
            for (int i = this.size - 1; i >= 0; i--)
                if (this.items[i] == value) return i;
        } else {
            for (int i = this.size - 1; i >= 0; i--)
                if (this.items[i].equals(value)) return i;
        }

        return -1;
    }

    /**
     * Removes a value from the Array by first locating it.
     *
     * @param value              Value to Remove
     * @param identityComparison Whether or not '==' should be used in comparisons
     * @return True if removed, false otherwise.
     */
    public boolean removeValue(T value, boolean identityComparison) {
        final int index = indexOf(value, identityComparison);

        if (index < 0) return false;

        removeIndex(index);
        return true;
    }

    /**
     * Remove element at a particular index.
     *
     * @param index Index of Array
     * @return Element at that index, after removal.
     */
    public T removeIndex(int index) {
        Preconditions.checkElementIndex(index, this.size);
        final T value = this.items[index];

        this.size--;
        if (this.ordered) {
            System.arraycopy(this.items, index + 1, this.items, index, this.size - index);
        } else {
            this.items[index] = this.items[this.size];
        }

        this.items[this.size] = null;
        return value;
    }

    /**
     * Remove a particular range of elements given a start and end index.
     *
     * @param start Start Index
     * @param end   End Index
     */
    public void removeRange(int start, int end) {
        Preconditions.checkElementIndex(end, this.size);
        Preconditions.checkElementIndex(start, end + 1);

        final int count = (end - start) + 1;
        if (this.ordered) {
            System.arraycopy(this.items, start + count, this.items, start, this.size - (start + count));
        } else {
            final int last = this.size - 1;
            for (int i = 0; i < count; i++) {
                this.items[start + 1] = this.items[last - i];
            }
        }

        this.size -= count;
    }

    /**
     * Remove all elements found in the given array from this array.
     *
     * @param array              Other Array
     * @param identityComparison Whether or not '==' should be used for comparisons
     * @return True if modified, false otherwise.
     */
    public boolean removeAll(@NotNull Array<? extends T> array, boolean identityComparison) {
        Preconditions.checkNotNull(array);

        final int startSize = this.size;
        if (identityComparison) {
            for (int i = 0; i < array.size; i++) {
                final T item = array.get(i);
                for (int j = 0; j < this.size; j++) {
                    if (item == this.items[j]) {
                        removeIndex(j);
                        this.size--;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < array.size; i++) {
                final T item = array.get(i);
                for (int j = 0; j < this.size; j++) {
                    if (item.equals(this.items[j])) {
                        removeIndex(j);
                        this.size--;
                        break;
                    }
                }
            }
        }

        return this.size != startSize;
    }

    /**
     * Pop an element off the top of this array. Identical to the Stack pop() operation.
     *
     * @return Popped Element
     */
    public T pop() {
        Preconditions.checkState(this.size != 0, "No Elements to Pop!");

        final T item = this.items[--this.size];
        this.items[this.size] = null;
        return item;
    }

    /**
     * Returns but does not remove the element at the top of this Array. Identical to the Stack peek() operation
     *
     * @return Element at top of Array
     */
    public T peek() {
        Preconditions.checkState(this.size != 0, "No Elements to Peek!");

        return this.items[this.size - 1];
    }

    /**
     * Returns but does not remove the first element of this Array.
     *
     * @return First element of array (index 0)
     */
    public T first() {
        Preconditions.checkState(this.size != 0, "No elements to Get!");

        return this.items[0];
    }

    /**
     * Removes all elements from this Array and sets size to 0. In order to shrink the explicit length of the internal
     * array, {@link #shrink()} should be called after this.
     */
    public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.items[i] = null;
        }

        this.size = 0;
    }

    /**
     * Shrinks the internal array down to the absolute minimal size while maintaining order and content.
     *
     * @return This Array for Chaining
     */
    public Array<T> shrink() {
        if (this.items.length != this.size) {
            resize(this.size);
        }

        return this;
    }

    /**
     * Ensure Capacity by adding additional capacity, if necessary the Array is resized/re-hashed to account for this.
     *
     * @param additionalCapacity Additional Capacity to add
     * @return this array for chaining
     */
    public Array<T> ensureCapacity(int additionalCapacity) {
        final int newSize = this.size + additionalCapacity;
        if (newSize > this.items.length) {
            resize(Math.max(8, newSize));
        }

        return this;
    }

    /**
     * Current Size of Array, as number of elements and not literal internal array length.
     *
     * @return Size of Array
     */
    public int size() {
        return size;
    }

    /**
     * @return True if initialized as an ordered array, false otherwise.
     */
    public boolean ordered() {
        return ordered;
    }

    @SuppressWarnings("unchecked")
    protected T[] resize(int newSize) {
        final T[] items = this.items;
        final T[] newItems = (T[]) newArray(items.getClass().getComponentType(), newSize);

        System.arraycopy(items, 0, newItems, 0, Math.min(this.size, newSize));
        this.items = newItems;
        return newItems;
    }

    // TODO Sort Methods

    // TODO Select Methods

    /**
     * Reverses the order of the array.
     *
     * @return This Array for Chaining
     */
    public Array<T> reverse() {
        for (int i = 0, last = this.size - 1, n = this.size / 2; i < n; i++) {
            final int j = last - i;
            final T temp = this.items[i];
            this.items[i] = this.items[j];
            this.items[j] = temp;
        }

        return this;
    }

    /**
     * Shuffles the order of the Array using a fisher-yates shuffle.
     *
     * @return This Array for Chaining
     */
    public Array<T> shuffle() {
        for (int i = this.size - 1; i >= 0; i--) {
            final int j = MathUtils.random(i);
            final T temp = this.items[i];
            this.items[i] = this.items[j];
            this.items[j] = temp;
        }

        return this;
    }

    @Override
    public Iterator<T> iterator() {
        if (this.iterable == null) {
            this.iterable = new ArrayIterable<>(this);
        }

        return this.iterable.iterator();
    }

    /**
     * Returns an iterable of elements that test true given the predicate.
     *
     * @param predicate Test Function
     * @return Iterable containing elements where P(element) = true
     */
    public Iterable<T> select(Predicate<T> predicate) {
        return Predicates.iterable(this.iterable, predicate);
    }

    /**
     * Truncates array to newSize. If size <= newSize then nothing happens
     *
     * @param newSize Size to truncate to
     */
    public void truncate(int newSize) {
        if (this.size <= newSize) return;

        for (int i = newSize; i < this.size; i++) {
            this.items[i] = null;
        }

        this.size = newSize;
    }

    /**
     * Returns a random element obtained using {@link MathUtils#random()}
     *
     * @return Random Element in Array
     */
    public T random() {
        Preconditions.checkState(this.size != 0, "No Elements to Select From!");

        return this.items[MathUtils.random(this.size - 1)];
    }

    /**
     * Unsafely copy items to a new array and cast that array to the desired type.
     *
     * @return Copy of Items
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) toArray(this.items.getClass().getComponentType());
    }

    /**
     * Copy items to a new array and return that
     *
     * @param type Type to cast to
     * @param <V>  Element Type
     * @return Copy of Items of Type V
     */
    public <V> V[] toArray(Class<V> type) {
        final V[] result = newArray(type, this.size);
        System.arraycopy(this.items, 0, result, 0, this.size);
        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = (37 * result) + this.size;
        result = (37 * result) + (this.items != null ? Arrays.hashCode(this.items) : 0);

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Array)) return false;

        final Array<?> arr = (Array<?>) object;
        if (this.size != arr.size) return false;

        final Object[] i1 = this.items;
        final Object[] i2 = arr.items;

        for (int i = 0; i < this.size; i++) {
            final Object o1 = i1[i];
            final Object o2 = i2[i];

            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        if (this.size == 0) return "[]";

        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.items[0]);

        for (int i = 0; i < this.size; i++) {
            sb.append(", ").append(this.items[i]);
        }

        sb.append(']');
        return sb.toString();
    }

    static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid = true;

        public ArrayIterator(Array<T> array) {
            this(array, true);
        }

        public ArrayIterator(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public boolean hasNext() {
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            return this.index < this.array.size;
        }

        @Override
        public T next() {
            Preconditions.checkElementIndex(this.index, this.array.size, String.valueOf(this.index));
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            return this.array.items[this.index++];
        }

        @Override
        public void remove() {
            Preconditions.checkState(this.allowRemove, "#remove() is Disallowed");

            this.index--;
            this.array.removeIndex(this.index);
        }

        public void reset() {
            this.index = 0;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    static class ArrayIterable<T> implements Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        private ArrayIterator<T> iterator1, iterator2;

        public ArrayIterable(Array<T> array) {
            this(array, true);
        }

        public ArrayIterable(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public Iterator<T> iterator() {
            if (this.iterator1 == null) {
                this.iterator1 = new ArrayIterator<>(this.array, this.allowRemove);
                this.iterator2 = new ArrayIterator<>(this.array, this.allowRemove);
            }
            if (!this.iterator1.valid) {
                this.iterator1.index = 0;
                this.iterator1.valid = true;
                this.iterator2.valid = false;
                return this.iterator1;
            }
            this.iterator2.index = 0;
            this.iterator2.valid = true;
            this.iterator1.valid = false;
            return this.iterator2;
        }
    }
}
