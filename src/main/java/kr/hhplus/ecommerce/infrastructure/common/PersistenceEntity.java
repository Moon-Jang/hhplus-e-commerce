package kr.hhplus.ecommerce.infrastructure.common;

public interface PersistenceEntity<E> {
    E toDomain();
}
