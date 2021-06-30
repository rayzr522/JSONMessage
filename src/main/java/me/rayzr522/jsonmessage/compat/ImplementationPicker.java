package me.rayzr522.jsonmessage.compat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ImplementationPicker<T> {
    private final List<Implementation<T>> implementations = new LinkedList<>();

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
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                });
    }

    public class Implementation<T> {
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
        T get() throws Throwable;
    }
}
