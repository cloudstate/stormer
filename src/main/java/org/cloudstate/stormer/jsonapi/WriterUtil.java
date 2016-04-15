/**
 */
package org.cloudstate.stormer.jsonapi;

import static org.cloudstate.stormer.jsonapi.JsonApiConstants.CIT;
import static org.cloudstate.stormer.jsonapi.JsonApiConstants.CIT_COLON;
import static org.cloudstate.stormer.jsonapi.JsonApiConstants.CIT_COLON_CIT;
import static org.cloudstate.stormer.jsonapi.JsonApiConstants.UTF_8;

import org.cloudstate.stormer.Entity;

import io.netty.buffer.ByteBuf;

/**
 */
final class WriterUtil {

	private static final byte[] SINGLE_PREFIX = toBytes("{\"data\":");
	private static final byte[] MULTI_PREFIX = toBytes("{\"data\":[");

	private static final byte[] LINKS_PREFIX = toBytes(",\"links\":{");
	private static final byte[] SELF = toBytes("\"self\":\"");

	private static final byte[] SINGLE_INCLUDES = toBytes(",\"includes\":[");
	private static final byte[] MULTI_INCLUDES = toBytes("],\"includes\":[");
	private static final byte[] POSTFIX = toBytes("]}");
	private static final byte[] NULL_ENTITY = toBytes("{\"data\":null,\"included\":[]}");

	@SuppressWarnings("unused")
	private static final byte[] ATTRIBUTES = toBytes(",\"attributes\":");

	private WriterUtil() {
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
