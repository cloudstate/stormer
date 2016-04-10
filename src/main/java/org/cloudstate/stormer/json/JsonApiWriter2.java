/**
 */
package org.cloudstate.stormer.json;

import io.netty.buffer.ByteBuf;

/**
 */
public final class JsonApiWriter2 {

	private JsonApiWriter2() {
		throw new UnsupportedOperationException();
	}

	public static TypeWriter jsonApi(final ByteBuf byteBuf) {
		return new Writer(byteBuf);
	}

	public static interface TypeWriter {
		IdWriter type(String type);
	}

	public static interface IdWriter {
		AttributesWriter id(String id);
	}

	public static interface AttributesWriter extends RelationsWriter {
		AttrWriter attributes();
	}

	public static interface AttrWriter extends RelationsWriter {
		AttrWriter add(String name, String value);
	}

	public static interface RelationsWriter {
		RelWriter relations();
	}

	public static interface RelWriter {
		RelWriter add();
	}

	private static final class Writer implements TypeWriter, IdWriter, AttributesWriter, AttrWriter, RelWriter, KeyValuePairWriter {

		private final ByteBuf byteBuf;

		protected Writer(final ByteBuf byteBuf) {
			this.byteBuf = byteBuf;
			byteBuf.writeByte('{');
		}

		@Override
		public IdWriter type(final String type) {
			write("type", type, byteBuf);
			return this;
		}

		@Override
		public AttributesWriter id(final String id) {
			byteBuf.writeByte(',');
			write("id", id, byteBuf);
			return this;
		}

		@Override
		public AttrWriter attributes() {
			return this;
		}

		@Override
		public AttrWriter add(final String name, final String value) {
			return this;
		}

		@Override
		public RelWriter add() {
			return this;
		}

		@Override
		public RelWriter relations() {
			return null;
		}

	}

}
