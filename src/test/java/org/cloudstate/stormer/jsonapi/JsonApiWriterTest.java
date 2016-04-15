package org.cloudstate.stormer.jsonapi;

import static java.nio.charset.Charset.forName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cloudstate.stormer.jsonapi.JsonApiWriter.jsonApi;

import java.nio.charset.Charset;

import org.cloudstate.stormer.Entity;
import org.junit.Before;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class JsonApiWriterTest {

	private static final Charset UTF_8 = forName("UTF-8");

	private ByteBuf byteBuf;

	@Before
	public void setUp() {
		byteBuf = Unpooled.buffer();
	}

	@Test
	public void empty() {
		jsonApi(byteBuf).end();
		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{}");
	}

	@Test
	public void typeAndId() {
		jsonApi(byteBuf).type("t").id("i").end();
		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{'type':'t','id':'i'}".replace("'", "\""));
	}

	@Test
	public void emptyAttributes() {
		jsonApi(byteBuf).type("t").id("i")
				.attributes().endAttr()
				.end();

		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{'type':'t','id':'i','attributes':{}}".replace("'", "\""));
	}

	@Test
	public void attribute() {
		jsonApi(byteBuf).type("t").id("i")
				.attributes().add("a", "1").endAttr()
				.end();

		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{'type':'t','id':'i','attributes':{'a':'1'}}".replace("'", "\""));
	}

	@Test
	public void attributes() {
		jsonApi(byteBuf).type("t").id("i")
				.attributes().add("a", "1").andAdd("b", 2).endAttr()
				.end();

		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{'type':'t','id':'i','attributes':{'a':'1','b':2}}".replace("'", "\""));
	}

	@Test
	public void emptyRelations() {
		jsonApi(byteBuf).type("t").id("i")
				.relations().endRels()
				.end();

		assertThat(byteBuf.toString(UTF_8)).isEqualTo("{'type':'t','id':'i','relationships':{}}".replace("'", "\""));
	}

	@Test
	public void relation() {
		jsonApi(byteBuf).type("t").id("i")
				.relations().add("father", new Person(13)).endRels()
				.end();

		assertThat(byteBuf.toString(UTF_8))
				.isEqualTo("{'type':'t','id':'i','relationships':{'father':{'data':{'type':'person','id':'13'}}}}".replace("'", "\""));
	}

	@Test
	public void relations() {
		jsonApi(byteBuf).type("t").id("i")
				.relations().add("father", new Person(13)).andAdd("mother", new Person(11)).endRels()
				.end();

		assertThat(byteBuf.toString(UTF_8)).isEqualTo(
				"{'type':'t','id':'i','relationships':{'father':{'data':{'type':'person','id':'13'}},'mother':{'data':{'type':'person','id':'11'}}}}"
						.replace("'", "\""));
	}

	private static final class Person implements Entity {

		private final String id;

		protected Person(final int id) {
			this.id = Integer.toString(id);
		}

		@Override
		public String getType() {
			return "person";
		}

		@Override
		public String getId() {
			return id;
		}

	}

}
