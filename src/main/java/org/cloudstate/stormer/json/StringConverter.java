/**
 */
package org.cloudstate.stormer.json;

import static org.cloudstate.stormer.json.JsonApiConstants.UTF_8;

/**
 */
abstract class StringConverter {

	byte[] toBytes(final String value) {
		return value.getBytes(UTF_8);
	}

	byte[] toBytes(final int value) {
		return Integer.toString(value).getBytes(UTF_8);
	}

}
