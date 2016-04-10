/**
 */
package org.cloudstate.rest.json;

import static org.cloudstate.rest.json.JsonApiConstants.UTF_8;

/**
 */
public interface StringConverter {

	default byte[] toBytes(final String value) {
		return value.getBytes(UTF_8);
	}

	default byte[] toBytes(final int value) {
		return Integer.toString(value).getBytes(UTF_8);
	}

}
