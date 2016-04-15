/**
 */
package org.cloudstate.stormer.json;

import static org.cloudstate.stormer.json.JsonApiConstants.CIT;
import static org.cloudstate.stormer.json.JsonApiConstants.CIT_COLON;
import static org.cloudstate.stormer.json.JsonApiConstants.CIT_COLON_CIT;
import static org.cloudstate.stormer.json.JsonApiConstants.UTF_8;

import org.cloudstate.stormer.Entity;

import io.netty.buffer.ByteBuf;

/**
 */
final class KeyValuePairWriter {

	private static final byte[] SINGLE_PREFIX = "{\"data\":".getBytes(UTF_8);
	private static final byte[] MULTI_PREFIX = "{\"data\":[".getBytes(UTF_8);

	private static final byte[] LINKS_PREFIX = ",\"links\":{".getBytes(UTF_8);
	private static final byte[] SELF = "\"self\":\"".getBytes(UTF_8);

	private static final byte[] SINGLE_INCLUDES = ",\"includes\":[".getBytes(UTF_8);
	private static final byte[] MULTI_INCLUDES = "],\"includes\":[".getBytes(UTF_8);
	private static final byte[] POSTFIX = "]}".getBytes(UTF_8);
	private static final byte[] NULL_ENTITY = "{\"data\":null,\"included\":[]}".getBytes(UTF_8);

	private static final byte[] ATTRIBUTES = ",\"attributes\":".getBytes(UTF_8);

	private KeyValuePairWriter() {
		throw new UnsupportedOperationException();
	}

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

	public static <T extends Entity> void self(final byte[] address, final T entity, final ByteBuf byteBuf) {
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

	static void writeValue(final String value, final ByteBuf byteBuf) {
		byteBuf.writeBytes(toBytes(value));
	}

	static void write(final String name, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON);
	}

	static void write(final String name, final String value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON_CIT)
				.writeBytes(toBytes(value)).writeByte(CIT);
	}

	static void write(final String name, final int value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON)
				.writeBytes(toBytes(value));
	}

	static byte[] toBytes(final String value) {
		return value.getBytes(UTF_8);
	}

	static byte[] toBytes(final int value) {
		return Integer.toString(value).getBytes(UTF_8);
	}

}
