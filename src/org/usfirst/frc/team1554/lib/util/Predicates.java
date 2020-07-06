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


package org.usfirst.frc.team1554.lib.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Convenience Class for managing Predicates and Collections of Predicates. Also allows for easy creation of predicate
 * constructs using boolean logic. <br />
 * <br />
 * Conditional Iterators and Conditional Iterables are also provided. These only return elements that are tested and
 * return true, as used in some collections provided by this library.
 */
public final class Predicates {

    /**
     * Returns a conditional Iterator that returns elements only if the provided predicate returns
     * true. If the predicate returns false then that element is skipped.
     *
     * @param iter      Iterator to apply the predicate to.
     * @param predicate Test Function
     * @param <T>       Type being tested
     * @return Conditional Iterator with the elements of the provided Iterator
     */
    public static <T> Iterator<T> iterator(Iterator<T> iter, Predicate<T> predicate) {
        return new PredicateIterator<>(iter, predicate);
    }

    /**
     * Returns a conditional Iterator that returns elements only if the provided predicate returns
     * true. If the predicate returns false then that element is skipped.
     *
     * @param iter      Iterable to apply the predicate to
     * @param predicate Test Function
     * @param <T>       Type being tested
     * @return Conditional Iterator with the elements of the provided Iterator
     */
    public static <T> Iterator<T> iterator(Iterable<T> iter, Predicate<T> predicate) {
        return new PredicateIterable<>(iter, predicate).iterator();
    }

    /**
     * Returns a conditional Iterable that returns elements only if the provided predicate returns
     * true. If the predicate returns false then that element is skipped.
     *
     * @param iter      Iterable to apply the predicate to
     * @param predicate Test Function
     * @param <T>       Type being tested
     * @return Conditional Iterable with the elements of the provided Iterator
     */
    public static <T> Iterable<T> iterable(Iterable<T> iter, Predicate<T> predicate) {
        return new PredicateIterable<>(iter, predicate);
    }

    /**
     * Returns a predicate that only returns true if the element tests as true in all provided predicates.
     *
     * @param predicates Predicates to AND together
     * @param <T>        Type being tested
     * @return A Compound Predicate resulting from the AND'ing of all provided Predicates.
     */
    @SafeVarargs
    public static <T> Predicate<T> and(Predicate<T>... predicates) {
        return new AndPredicate<>(defensiveCopy(predicates));
    }

    /**
     * Returns a predicate that only returns true if the element tests as true in all provided predicates.
     *
     * @param predicates Predicates to AND together
     * @param <T>        Type being tested
     * @return A Compound Predicate resulting from the AND'ing of all provided Predicates.
     */
    public static <T> Predicate<T> and(Iterable<Predicate<T>> predicates) {
        return new AndPredicate<>(defensiveCopy(predicates));
    }

    /**
     * Returns a predicate that returns true if ANY of the provided predicates test true for an element.
     *
     * @param predicates Predicates to OR together
     * @param <T>        Type being tested
     * @return A Compound Predicate resulting from the OR'ing of all provided Predicates.
     */
    @SafeVarargs
    public static <T> Predicate<T> or(Predicate<T>... predicates) {
        return new OrPredicate<>(defensiveCopy(predicates));
    }

    /**
     * Returns a predicate that returns true if ANY of the provided predicates test true for an element.
     *
     * @param predicates Predicates to OR together
     * @param <T>        Type being tested
     * @return A Compound Predicate resulting from the OR'ing of all provided Predicates.
     */
    public static <T> Predicate<T> or(Iterable<Predicate<T>> predicates) {
        return new OrPredicate<>(defensiveCopy(predicates));
    }

    /**
     * A Predicate that ALWAYS returns True
     *
     * @param <T> Type being tested
     * @return An Always True Predicate
     */
    public static <T> Predicate<T> alwaysTrue() {
        return (t) -> true;
    }

    /**
     * A Predicate that ALWAYS returns False
     *
     * @param <T> Type being tested
     * @return An Always False Predicate
     */
    public static <T> Predicate<T> alwaysFalse() {
        return (t) -> false;
    }

    /**
     * A Predicate that returns true only if the element being tested is determined to be equal to the target via
     * {@link Object#equals(Object) target.equals()}. If the target is null then the Null Predicate is returned using
     * {@link #isNull()}.
     *
     * @param target Element to test against
     * @param <T>    Type being tested
     * @return A Predicate that returns the result of target.equals()
     */
    public static <T> Predicate<T> equalTo(@Nullable T target) {
        if (target == null)
            return isNull();

        return new EqualsPredicate<>(target);
    }

    /**
     * A Predicate that returns true only if the element being tested is determined to be the identity of the target
     * via the '==' operator. If the target is null then the Null Predicate is returned using {@link #isNull()}.
     *
     * @param target Element to test against
     * @param <T>    Type being tested
     * @return The result of target == element
     */
    public static <T> Predicate<T> identityOf(@Nullable T target) {
        if (target == null)
            return isNull();

        return new IdentityPredicate<>(target);
    }

    /**
     * A Predicate that determines if an element exists inside a given Collection using {@link Collection#contains(Object)}.
     *
     * @param collect Collection to test against
     * @param <T>     Type being tested
     * @return The result of Collection.contains
     */
    public static <T> Predicate<T> in(@NotNull Collection<? extends T> collect) {
        Preconditions.checkNotNull(collect);

        return collect::contains;
    }

    /**
     * A Predicate that determines if a String element contains the specified target subsequence.
     *
     * @param subSequence Target Subsequence
     * @return Result of t.contains(target)
     */
    public static Predicate<String> contains(CharSequence subSequence) {
        return (t) -> t.contains(subSequence);
    }

    /**
     * A Predicate that determines if an object is an instance of a class.
     *
     * @param klass Class Reference Type
     * @return The Result of klass.isInstance(element)
     */
    public static Predicate<Object> isInstanceOf(Class<?> klass) {
        return klass::isInstance;
    }

    /**
     * A Predicate that determines if a class is castable to the target class.
     *
     * @param klass Class Reference Type
     * @return The result of klass.isAssignableFrom(element)
     */
    public static Predicate<Class<?>> isAssignableFrom(Class<?> klass) {
        return klass::isAssignableFrom;
    }

    /**
     * A Predicate that determines if a {@link CharSequence} contains a specified RegExp Pattern. This is mostly
     * the result of {@link Matcher#find()}
     *
     * @param pattern Regular Expression
     * @return A Predicate that returns the result of pattern.matcher(element).find()
     */
    public static Predicate<CharSequence> containsPattern(Pattern pattern) {
        return new RegexPredicate(pattern);
    }

    /**
     * A Predicate that determines if a {@link CharSequence} contains a specified RegExp Pattern. This is mostly
     * the result of {@link Matcher#find()}.
     *
     * @param pattern Regular Expression
     * @return A Predicate that returns the result of pattern.matcher(element).find()
     */
    public static Predicate<CharSequence> containsPattern(String pattern) {
        return new RegexPredicate(Pattern.compile(pattern));
    }

    /**
     * A Predicate representing the composition of the Test Function and Object Function. <br /> <br />
     * <pre>
     *     Let P(x) = Predicate Function
     *     Let F(x) = Object Function
     *     Let C(x) = P(F(x))
     * </pre>
     * <p>
     * This function returns C(x).
     *
     * @param predicate Test Function
     * @param function  Object Function
     * @param <A>       Type being tested
     * @param <B>       Type returned by Object Function
     * @return Composition of Test and Object Function
     */
    public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, B> function) {
        return (t) -> predicate.test(function.apply(t));
    }

    /**
     * A Predicate returning the result of == null
     *
     * @return The result of element == null
     */
    public static <T> Predicate<T> isNull() {
        return (t) -> t == null;
    }

    /**
     * A Predicate returning the result of != null
     *
     * @return The result of element != null
     */
    public static <T> Predicate<T> isNotNull() {
        return (t) -> t != null;
    }

    @SafeVarargs
    private static <T> List<T> defensiveCopy(T... elements) {
        return defensiveCopy(Arrays.asList(elements));
    }

    private static <T> List<T> defensiveCopy(Iterable<T> iter) {
        ArrayList<T> list = new ArrayList<>();

        for (T element : iter) {
            list.add(Preconditions.checkNotNull(element));
        }

        return list;
    }

    static class PredicateIterator<T> implements Iterator<T> {

        Iterator<T> iterator;
        Predicate<T> predicate;
        boolean end = false;
        boolean peeked = false;
        T next = null;

        PredicateIterator(Iterator<T> iter, Predicate<T> predicate) {
            set(iter, predicate);
        }

        public void set(Iterator<T> iter, Predicate<T> predicate) {
            this.iterator = iter;
            this.predicate = predicate;
        }

        @Override
        public boolean hasNext() {
            if (end) return false;
            if (next != null) return true;

            peeked = true;
            while (iterator.hasNext()) {
                final T n = iterator.next();
                if (predicate.test(n)) {
                    next = n;
                    return true;
                }
            }
            end = true;
            return false;
        }

        @Override
        public T next() {
            if ((next == null) && !hasNext()) return null;

            final T res = next;
            next = null;
            peeked = false;
            return res;
        }

        @Override
        public void remove() {
            if (peeked)
                throw new RuntimeException("Cannot Remove between hasNext() and next()!");

            iterator.remove();
        }

    }

    static class PredicateIterable<T> implements Iterable<T> {
        Iterable<T> iterable;
        Predicate<T> predicate;
        PredicateIterator<T> iterator;

        PredicateIterable(Iterable<T> iter, Predicate<T> predicate) {
            set(iter, predicate);
        }

        public void set(Iterable<T> iter, Predicate<T> predicate) {
            this.iterable = iter;
            this.predicate = predicate;
        }

        @Override
        public Iterator<T> iterator() {
            if (iterator == null) {
                iterator = new PredicateIterator<>(iterable.iterator(), predicate);
            } else {
                iterator.set(iterable.iterator(), predicate);
            }

            return iterator;
        }
    }

    static class RegexPredicate implements Predicate<CharSequence> {

        private Pattern pattern;

        RegexPredicate(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean test(CharSequence charSequence) {
            return pattern.matcher(charSequence).find(0);
        }
    }

    static class AndPredicate<T> implements Predicate<T> {

        private final List<Predicate<T>> predicates;

        AndPredicate(List<Predicate<T>> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(T t) {
            for (Predicate<T> predicate : predicates) {
                if (!predicate.test(t))
                    return false;
            }

            return true;
        }
    }

    static class OrPredicate<T> implements Predicate<T> {

        private final List<Predicate<T>> predicates;

        OrPredicate(List<Predicate<T>> predicates) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(T t) {
            for (Predicate<T> predicate : predicates) {
                if (predicate.test(t))
                    return true;
            }

            return false;
        }
    }

    static class EqualsPredicate<T> implements Predicate<T> {

        private final T target;

        EqualsPredicate(T target) {
            this.target = target;
        }

        @Override
        public boolean test(T t) {
            return target.equals(t);
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return target.equals(o);
        }

    }

    static class IdentityPredicate<T> implements Predicate<T> {

        private final T target;

        IdentityPredicate(T target) {
            this.target = target;
        }

        @Override
        public boolean test(T t) {
            return t == target;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o == target;
        }
    }

}
