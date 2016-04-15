/**
 */
package org.cloudstate.stormer.json;

import static io.advantageous.boon.json.JsonFactory.create;
import static org.cloudstate.stormer.json.JsonApiConstants.UTF_8;

import java.util.Iterator;
import java.util.Optional;

import org.cloudstate.stormer.Codec;
import org.cloudstate.stormer.Entity;

import io.advantageous.boon.json.JsonParserAndMapper;
import io.netty.buffer.ByteBuf;

/**
 */
public abstract class JsonApiCodec<T extends Entity> implements Codec<T> {

	private static final JsonParserAndMapper mapper = create().parser();

	private byte[] address;

	@Override
	public final void setAddres(final byte[] address) {
		this.address = address;
	}

	@Override
	public final void encode(final Optional<T> entity, final ByteBuf byteBuf) {
		if (entity.isPresent()) {
			final T e = entity.get();

			KeyValuePairWriter.singlePrefix(byteBuf);
			encode(e, JsonApiWriter.jsonApi(byteBuf));

			KeyValuePairWriter.links(byteBuf);
			KeyValuePairWriter.self(address, e, byteBuf);
			KeyValuePairWriter.linksEnd(byteBuf);

			KeyValuePairWriter.singleIncludes(byteBuf);
			KeyValuePairWriter.postfix(byteBuf);
		} else {
			KeyValuePairWriter.nullEntity(byteBuf);
		}
	}

	@Override
	public final void encode(final Iterable<T> entities, final ByteBuf byteBuf) {
		KeyValuePairWriter.multiPrefix(byteBuf);

		final Iterator<T> it = entities.iterator();

		if (it.hasNext()) {
			encode(it.next(), JsonApiWriter.jsonApi(byteBuf));
		}

		while (it.hasNext()) {
			byteBuf.writeByte(',');
			encode(it.next(), JsonApiWriter.jsonApi(byteBuf));
		}

		KeyValuePairWriter.multiIncludes(byteBuf);
		KeyValuePairWriter.postfix(byteBuf);
	}

	@Override
	public final T decode(final ByteBuf byteBuf) {
		if (byteBuf.hasArray()) {
			return decode(new JsonApiReader(mapper.parseMap(byteBuf.array(), UTF_8)));
		}

		final byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);

		return decode(new JsonApiReader(mapper.parseMap(bytes, UTF_8)));
	}

	public abstract void encode(final T entity, final JsonApiWriter.TypeWriter json);

	public abstract T decode(JsonApiReader json);

}
