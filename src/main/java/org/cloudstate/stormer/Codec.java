/**
 */
package org.cloudstate.stormer;

import java.util.Optional;

import io.netty.buffer.ByteBuf;

/**
 */
public interface Codec<T> {

	/**
	 * Serializes an entity to a {@code ByteBuf}. In cases where a particular entity was queried for but not found, the entity is {@code empty} and
	 * the converter should act accordingly.
	 * @param entity, might be {@code empty}.
	 * @param byteBuf the {@code ByteBuf} to serialize the entity to.
	 */
	void encode(Optional<T> entity, ByteBuf byteBuf);

	void encode(Iterable<T> entities, ByteBuf byteBuf);

	T decode(ByteBuf byteBuf);

	void setAddres(byte[] address);

}
