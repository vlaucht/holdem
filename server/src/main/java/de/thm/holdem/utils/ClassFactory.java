package de.thm.holdem.utils;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ClassFactory<T> {
    private final Class<T> clazz;

    public ClassFactory(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T createInstance(Object... constructorArgs) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = Arrays.stream(constructorArgs)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .toArray(Class[]::new);

        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodException("Constructor not found for the provided argument types.");
        }

        constructor.setAccessible(true);

        return constructor.newInstance(constructorArgs);
    }
}
