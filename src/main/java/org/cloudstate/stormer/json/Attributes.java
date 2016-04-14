/**
 */
package org.cloudstate.stormer.json;

/**
 */
public class Attributes extends AbstractJsonApi {

	protected final JsonApiWriter api;

	public Attributes(final JsonApiWriter api) {
		super(api.byteBuf);
		this.api = api;
	}

	public Attributes add(final String name, final String value) {
		api.byteBuf.writeByte('{');
		write(name, value, api.byteBuf);
		return new CommaAttributes(api);
	}

	public Attributes add(final String name, final int value) {
		api.byteBuf.writeByte('{');
		write(name, value, api.byteBuf);
		return new CommaAttributes(api);
	}

	@Override
	public JsonApiWriter end() {
		api.byteBuf.writeByte('}');
		return api;
	}

	private static final class CommaAttributes extends Attributes {

		public CommaAttributes(final JsonApiWriter api) {
			super(api);
		}

		@Override
		public Attributes add(final String name, final String value) {
			api.byteBuf.writeByte(',');
			write(name, value, api.byteBuf);
			return this;
		}

		@Override
		public Attributes add(final String name, final int value) {
			api.byteBuf.writeByte(',');
			write(name, value, api.byteBuf);
			return this;
		}

	}

}
