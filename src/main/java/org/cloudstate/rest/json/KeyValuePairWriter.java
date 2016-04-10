/**
 */
package org.cloudstate.rest.json;

import static org.cloudstate.rest.json.JsonApiConstants.CIT;
import static org.cloudstate.rest.json.JsonApiConstants.CIT_COLON;
import static org.cloudstate.rest.json.JsonApiConstants.CIT_COLON_CIT;

import io.netty.buffer.ByteBuf;

/**
 */
interface KeyValuePairWriter extends StringConverter {

	default void write(final String name, final String value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON_CIT)
				.writeBytes(toBytes(value)).writeByte(CIT);
	}

	default void write(final String name, final int value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON)
				.writeBytes(toBytes(value));
	}

}
