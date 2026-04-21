package rizzoweb.spring.jpa.specifications;

import org.jspecify.annotations.NonNull;

public interface EntityManagerWrapper {

    <T> @NonNull T persistAndFlush(T entity);
    void clear();

}
