package com.something;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class App {
    static boolean isPublic(final Method m) {
        return Modifier.isPublic(m.getModifiers()) || m.isDefault();
    }

    static boolean isAbstract(final Method m) {
        return Modifier.isAbstract(m.getModifiers());
    }

    static String getPartialSignature(final Method m) {
        return String.format(
                "%s: (%s) -> %s",
                m.getName(),
                Arrays.stream(m.getParameterTypes())
                        .map(Class::getName)
                        .collect(Collectors.joining(", ")),
                m.getReturnType().getName());
    }

    public static void main(final String[] args) throws ClassNotFoundException {
        final String targetClass = args[0];

        final Class<?> cls = Class.forName(targetClass);
        final boolean isInterface = cls.isInterface();

        final Set<String> publicMethods = new TreeSet<>();
        final Set<String> otherMethods = new TreeSet<>();

        // look for public methods declared by the class itself
        for (final Method m : cls.getDeclaredMethods())
            if (isPublic(m) && (!isAbstract(m) || isInterface) && !m.isSynthetic()
            ) {
                final String signature = getPartialSignature(m);
                publicMethods.add(signature);
                System.out.println("declared:> " + signature);
            }

        // look for other public methods
        for (final Method m : cls.getMethods()) {
            final String signature = getPartialSignature(m);

            if (isPublic(m) && (!isAbstract(m) || isInterface) && !m.isSynthetic()
                    &&
                    m.getDeclaringClass() != Object.class
                    &&
                    !publicMethods.contains(signature)
            ) {
                otherMethods.add(signature);
                System.out.println("inherited:> " + signature);
            }
        }

        System.out.println("---");
        System.out.println("Target: " + cls);
        System.out.println("=");
        System.out.println("Declared public methods: " + publicMethods.size());
        System.out.println("Public constructors: " + cls.getConstructors().length);
        System.out.println("Inherited non-overridden public methods (non-Object): " + otherMethods.size());
    }
}
