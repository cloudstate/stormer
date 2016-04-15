/**
 */
package org.cloudstate.stormer.jsonapi;

import static java.nio.charset.Charset.forName;

import java.nio.charset.Charset;

/**
 */
final class JsonApiConstants {

	static final Charset UTF_8 = forName("UTF-8");

	static final int CIT = '"';
	static final byte[] CIT_COLON_CIT = "\":\"".getBytes(UTF_8);
	static final byte[] CIT_COLON = "\":".getBytes(UTF_8);

}
