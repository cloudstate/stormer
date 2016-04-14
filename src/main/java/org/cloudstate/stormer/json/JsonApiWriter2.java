/**
 */
package org.cloudstate.stormer.json;

import org.cloudstate.stormer.Entity;

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

	public static interface TypeWriter extends EndWriter {
		IdWriter type(String type);
	}

	public static interface IdWriter {
		AttrsRelsWriter id(String id);
	}

	public static interface AttrsRelsWriter extends AttributesWriter, RelationsWriter {
		// Empty
	}

	public static interface AttributesWriter extends EndWriter {
		AttrWriter attributes();
	}

	public static interface AttrWriter extends EndAttrWriter {
		AndAttrWriter add(String name, String value);

		AndAttrWriter add(String name, int value);
	}

	public static interface AndAttrWriter extends EndAttrWriter {
		AndAttrWriter andAdd(String name, String value);

		AndAttrWriter andAdd(String name, int value);
	}

	public static interface EndAttrWriter {
		RelationsWriter endAttr();
	}

	public static interface RelationsWriter extends EndWriter {
		RelWriter relations();
	}

	public static interface RelWriter extends EndRelWriter {
		<T extends Entity> AndRelWriter add(String name, T entity);
	}

	public static interface AndRelWriter extends EndRelWriter {
		<T extends Entity> AndRelWriter andAdd(String name, T entity);
	}

	public static interface EndRelWriter {
		EndWriter endRels();
	}

	public static interface EndWriter {
		void end();
	}

	private static final class Writer extends KeyValuePairWriter
			implements TypeWriter, IdWriter, AttrsRelsWriter, AttrWriter, AndAttrWriter, RelWriter, AndRelWriter {

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
		public AttrsRelsWriter id(final String id) {
			byteBuf.writeByte(',');
			write("id", id, byteBuf);
			return this;
		}

		@Override
		public AttrWriter attributes() {
			writeValue(",\"attributes\":{", byteBuf);
			return this;
		}

		@Override
		public AndAttrWriter add(final String name, final String value) {
			write(name, value, byteBuf);
			return this;
		}

		@Override
		public AndAttrWriter add(final String name, final int value) {
			write(name, value, byteBuf);
			return this;
		}

		@Override
		public AndAttrWriter andAdd(final String name, final String value) {
			byteBuf.writeByte(',');
			write(name, value, byteBuf);
			return this;
		}

		@Override
		public AndAttrWriter andAdd(final String name, final int value) {
			byteBuf.writeByte(',');
			write(name, value, byteBuf);
			return this;
		}

		@Override
		public RelationsWriter endAttr() {
			byteBuf.writeByte('}');
			return this;
		}

		@Override
		public RelWriter relations() {
			writeValue(",\"relationships\":{", byteBuf);
			return this;
		}

		@Override
		public <T extends Entity> AndRelWriter add(final String name, final T entity) {
			return this;
		}

		@Override
		public <T extends Entity> AndRelWriter andAdd(final String name, final T entity) {
			return this;
		}

		@Override
		public EndWriter endRels() {
			byteBuf.writeByte('}');
			return this;
		}

		@Override
		public void end() {
			byteBuf.writeByte('}');
		}

	}

}
