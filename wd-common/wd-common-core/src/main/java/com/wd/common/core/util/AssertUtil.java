package com.wd.common.core.util;


import com.wd.common.core.annotions.Nullable;
import com.wd.common.core.exception.PromptException;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AssertUtil {
    public static void notNull(Object object) {
        notNull(object, "参数不能为null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new PromptException(message);
        }
    }

    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }

    public static void notNull(Object object, String message, Object... vars) {
        if (object == null) {
            throw new PromptException(String.format(message, vars));
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new PromptException(message);
        }
    }

    public static void isNull(Object object, Supplier<String> messageSupplier) {
        if (object != null) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }

    public static void isFalse(boolean expression, String message, Object... vars) {
        if (expression) {
            throw new PromptException(String.format(message, vars));
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new PromptException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new PromptException(message);
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }

    public static void isTrue(boolean expression, PromptException promptException) {
        if (!expression) {
            throw promptException;
        }
    }

    public static void isTrue(boolean expression, String message, Object... vars) {
        if (!expression) {
            throw new PromptException(String.format(message, vars));
        }
    }

    public static void assertMessage(String message) {
        throw new PromptException(message);
    }

    public static void assertMessage(String message, Object... vars) {
        throw new PromptException(String.format(message, vars));
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws PromptException if the collection is {@code null} or
     *                                 contains no elements
     */
    public static void notEmpty(@Nullable Collection<?> collection, String message) {
        if (isEmpty(collection)) {
            throw new PromptException(message);
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">
     * Assert.notEmpty(collection, () -&gt; "The " + collectionType + " collection must contain elements");
     * </pre>
     *
     * @param collection      the collection to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws PromptException if the collection is {@code null} or
     *                                 contains no elements
     * @since 5.0
     */
    public static void notEmpty(@Nullable Collection<?> collection, Supplier<String> messageSupplier) {
        if (isEmpty(collection)) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * Assert that a Map contains entries; that is, it must not be {@code null}
     * and must contain at least one entry.
     * <pre class="code">Assert.notEmpty(map, "Map must contain entries");</pre>
     *
     * @param map     the map to check
     * @param message the exception message to use if the assertion fails
     * @throws PromptException if the map is {@code null} or contains no entries
     */
    public static void notEmpty(@Nullable Map<?, ?> map, String message) {
        if (isEmpty(map)) {
            throw new PromptException(message);
        }
    }

    /**
     * Assert that a Map contains entries; that is, it must not be {@code null}
     * and must contain at least one entry.
     * <pre class="code">
     * Assert.notEmpty(map, () -&gt; "The " + mapType + " map must contain entries");
     * </pre>
     *
     * @param map             the map to check
     * @param messageSupplier a supplier for the exception message to use if the
     *                        assertion fails
     * @throws PromptException if the map is {@code null} or contains no entries
     * @since 5.0
     */
    public static void notEmpty(@Nullable Map<?, ?> map, Supplier<String> messageSupplier) {
        if (isEmpty(map)) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    @Nullable
    private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

    public static void hasText(@Nullable String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new PromptException(message);
        }
    }

    public static void hasText(@Nullable String text, Supplier<String> messageSupplier) {
        if (!StringUtils.hasText(text)) {
            throw new PromptException(nullSafeGet(messageSupplier));
        }
    }
}
