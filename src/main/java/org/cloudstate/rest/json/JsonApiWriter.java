/**
 */
package org.cloudstate.rest.json;

import io.netty.buffer.ByteBuf;

/**
 */
public final class JsonApiWriter extends AbstractJsonApi implements KeyValuePairWriter {

	private static final byte[] SINGLE_PREFIX = "{\"data\":".getBytes(UTF_8);
	private static final byte[] MULTI_PREFIX = "{\"data\":[".getBytes(UTF_8);

	private static final byte[] LINKS_PREFIX = ",\"links\":{".getBytes(UTF_8);
	private static final byte[] SELF = "\"self\":\"".getBytes(UTF_8);

	private static final byte[] SINGLE_INCLUDES = ",\"includes\":[".getBytes(UTF_8);
	private static final byte[] MULTI_INCLUDES = "],\"includes\":[".getBytes(UTF_8);
	private static final byte[] POSTFIX = "]}".getBytes(UTF_8);
	private static final byte[] NULL_ENTITY = "{\"data\":null,\"included\":[]}".getBytes(UTF_8);

	private static final byte[] ATTRIBUTES = ",\"attributes\":".getBytes(UTF_8);

	public static void nullEntity(final ByteBuf byteBuf) {
		byteBuf.writeBytes(NULL_ENTITY);
	}

	public static void singlePrefix(final ByteBuf byteBuf) {
		byteBuf.writeBytes(SINGLE_PREFIX);
	}

	public static void multiPrefix(final ByteBuf byteBuf) {
		byteBuf.writeBytes(MULTI_PREFIX);
	}

	public static void links(final ByteBuf byteBuf) {
		byteBuf.writeBytes(LINKS_PREFIX);
	}

	public static <T extends JsonApiEntity> void self(final byte[] address, final T entity, final ByteBuf byteBuf) {
		byteBuf.writeBytes(SELF);
		byteBuf.writeBytes(address);
		byteBuf.writeByte('/');
		byteBuf.writeBytes(entity.getType().getBytes(UTF_8));
		byteBuf.writeByte('/');
		byteBuf.writeBytes(entity.getId().getBytes(UTF_8));
		byteBuf.writeByte('\"');
	}

	public static void linksEnd(final ByteBuf byteBuf) {
		byteBuf.writeByte('}');
	}

	public static void singleIncludes(final ByteBuf byteBuf) {
		byteBuf.writeBytes(SINGLE_INCLUDES);
	}

	public static void multiIncludes(final ByteBuf byteBuf) {
		byteBuf.writeBytes(MULTI_INCLUDES);
	}

	public static void postfix(final ByteBuf byteBuf) {
		byteBuf.writeBytes(POSTFIX);
	}

	public JsonApiWriter(final ByteBuf byteBuf) {
		super(byteBuf);
		byteBuf.writeByte('{');
	}

	public JsonApiWriter type(final String type) {
		write("type", type, byteBuf);
		return this;
	}

	public JsonApiWriter id(final String id) {
		byteBuf.writeByte(',');
		write("id", id, byteBuf);
		return this;
	}

	public Attributes attributes() {
		byteBuf.writeBytes(ATTRIBUTES);
		return new Attributes(this);
	}

	@Override
	public JsonApiWriter end() {
		byteBuf.writeByte('}');
		return this;
	}

}
