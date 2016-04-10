package org.cloudstate.stormer.json;

import java.util.Map;

public final class JsonApiReader {

	private final Map<String, Object> json;
	private final Map<String, Object> data;
	private final Map<String, Object> attributes;

	@SuppressWarnings("unchecked")
	public JsonApiReader(final Map<String, Object> json) {
		this.json = json;
		data = (Map<String, Object>) json.get("data");
		attributes = (Map<String, Object>) data.get("attributes");
	}

	public String type() {
		return (String) data.get("type");
	}

	public String id() {
		return (String) data.get("id");
	}

	public String getAttributeAsString(final String name) {
		return (String) attributes.get(name);
	}

}
