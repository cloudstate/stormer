/**
 */
package org.cloudstate.stormer.json;

import static org.cloudstate.stormer.json.JsonApiConstants.CIT;
import static org.cloudstate.stormer.json.JsonApiConstants.CIT_COLON;
import static org.cloudstate.stormer.json.JsonApiConstants.CIT_COLON_CIT;

import io.netty.buffer.ByteBuf;

/**
 */
abstract class KeyValuePairWriter extends StringConverter {

	void writeValue(final String value, final ByteBuf byteBuf) {
		byteBuf.writeBytes(toBytes(value));
	}

	void write(final String name, final String value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON_CIT)
				.writeBytes(toBytes(value)).writeByte(CIT);
	}

	void write(final String name, final int value, final ByteBuf byteBuf) {
		byteBuf.writeByte(CIT).writeBytes(toBytes(name))
				.writeBytes(CIT_COLON)
				.writeBytes(toBytes(value));
	}

}
