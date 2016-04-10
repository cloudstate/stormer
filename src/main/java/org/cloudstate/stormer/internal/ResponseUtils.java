/**
 */
package org.cloudstate.stormer.internal;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static java.nio.charset.Charset.forName;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 */
public final class ResponseUtils {

	private static final Charset UTF_8 = forName("UTF-8");

	private static final byte[] MSG_BAD_REQUEST = "{\"code\": 400,\"message\": \"HTTP 400 Bad Request\"}".getBytes(UTF_8);
	private static final byte[] MSG_NOT_FOUND = "{\"code\": 404,\"message\": \"HTTP 404 Not Found\"}".getBytes(UTF_8);

	private ResponseUtils() {
		throw new UnsupportedOperationException();
	}

	public static HttpResponseStatus badRequest(final ByteBuf byteBuf) {
		byteBuf.clear().writeBytes(MSG_BAD_REQUEST);
		return BAD_REQUEST;
	}

	public static HttpResponseStatus notFound(final ByteBuf byteBuf) {
		byteBuf.clear().writeBytes(MSG_NOT_FOUND);
		return NOT_FOUND;
	}

}
