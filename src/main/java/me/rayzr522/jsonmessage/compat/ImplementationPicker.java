package me.rayzr522.jsonmessage.compat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ImplementationPicker<T> {
    private final List<Implementation<T>> implementations = new LinkedList<>();

    /**
     * Adds an implementation to the list of implementations that can be chosen from.
     *
     * @param minimumVersion minimum major version that this implementation applies to (inclusive)
     * @param maximumVersion maximum major version that this implementation applies to (inclusive)
     * @param factory factory method that can lazily construct an instance of this implementation
     * @return This {@link ImplementationPicker} reference.
     */
    public ImplementationPicker<T> addImplementation(int minimumVersion, int maximumVersion, ImplementationFactory<T> factory) {
        implementations.add(new Implementation<>(minimumVersion, maximumVersion, factory));
        return this;
    }

    public Optional<T> getImplementation(int version) {
        return implementations.stream()
                .filter(impl -> version >= impl.minimumVersion && version <= impl.maximumVersion)
                .findFirst()
                .map(impl -> {
                    try {
                        return impl.factory.get();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return null;
                    }
                });
    }

    public static class Implementation<T> {
        private final int minimumVersion;
        private final int maximumVersion;
        private final ImplementationFactory<T> factory;

        private Implementation(int minimumVersion, int maximumVersion, ImplementationFactory<T> factory) {
            this.minimumVersion = minimumVersion;
            this.maximumVersion = maximumVersion;
            this.factory = factory;
        }
    }

    public interface ImplementationFactory<T> {
        T get() throws Exception;
    }
}
