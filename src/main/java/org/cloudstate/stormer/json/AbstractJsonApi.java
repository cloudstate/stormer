/**
 */
package org.cloudstate.stormer.json;

import static java.nio.charset.Charset.forName;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

/**
 */
public abstract class AbstractJsonApi {

	protected static final Charset UTF_8 = forName("UTF-8");

	protected final ByteBuf byteBuf;

	public AbstractJsonApi(final ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public abstract AbstractJsonApi end();

}
