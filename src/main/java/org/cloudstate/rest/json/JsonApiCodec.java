/**
 */
package org.cloudstate.rest.json;

import static io.advantageous.boon.json.JsonFactory.create;
import static org.cloudstate.rest.json.JsonApiConstants.UTF_8;
import static org.cloudstate.rest.json.JsonApiWriter.nullEntity;

import java.util.Iterator;
import java.util.Optional;

import org.cloudstate.rest.Codec;

import io.advantageous.boon.json.JsonParserAndMapper;
import io.netty.buffer.ByteBuf;

/**
 */
public abstract class JsonApiCodec<T extends JsonApiEntity> implements Codec<T> {

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

			JsonApiWriter.singlePrefix(byteBuf);
			encode(e, new JsonApiWriter(byteBuf));

			JsonApiWriter.links(byteBuf);
			JsonApiWriter.self(address, e, byteBuf);
			JsonApiWriter.linksEnd(byteBuf);

			JsonApiWriter.singleIncludes(byteBuf);
			JsonApiWriter.postfix(byteBuf);
		} else {
			nullEntity(byteBuf);
		}
	}

	@Override
	public final void encode(final Iterable<T> entities, final ByteBuf byteBuf) {
		JsonApiWriter.multiPrefix(byteBuf);

		final Iterator<T> it = entities.iterator();

		if (it.hasNext()) {
			encode(it.next(), new JsonApiWriter(byteBuf));
		}

		while (it.hasNext()) {
			byteBuf.writeByte(',');
			encode(it.next(), new JsonApiWriter(byteBuf));
		}

		JsonApiWriter.multiIncludes(byteBuf);
		JsonApiWriter.postfix(byteBuf);
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

	public abstract void encode(final T entity, final JsonApiWriter json);

	public abstract T decode(JsonApiReader json);

}
