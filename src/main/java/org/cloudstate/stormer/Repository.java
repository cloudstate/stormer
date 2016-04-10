/**
 */
package org.cloudstate.stormer;

import java.util.Collection;
import java.util.Optional;

/**
 */
public interface Repository<T> {

	T store(T entity);

	Optional<T> load(String id);

	Collection<T> load(Collection<String> ids);

	Collection<T> loadAll();

}
