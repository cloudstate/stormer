/**
 */
package org.cloudstate.rest.internal;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.util.Optional.of;
import static org.cloudstate.rest.internal.ResponseUtils.badRequest;
import static org.cloudstate.rest.internal.ResponseUtils.notFound;

import java.util.ArrayList;
import java.util.List;

import org.cloudstate.rest.Codec;
import org.cloudstate.rest.Repository;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 */
public class ResourceHandler<T> {

	private final String resource;
	private final int resourceLength;

	private final Repository<T> repository;
	private final Codec<T> codec;

	private ResourceHandler(final String resource, final Repository<T> repository, final Codec<T> codec) {
		this.resource = resource;
		resourceLength = resource.length();

		this.repository = repository;
		this.codec = codec;
	}

	public static <T> ResourceHandler<T> create(final String url, final Repository<T> repository, final Codec<T> codec) {
		return new ResourceHandler<>(url, repository, codec);
	}

	public final boolean matches(final String url) {
		return url.startsWith(this.resource);
	}

	public HttpResponseStatus handleGet(final byte[] url, final ByteBuf response) {
		final int length = url.length;

		if (resourceLength == length) {
			return handleGetAll(response);
		}

		int i = resourceLength;

		final char c = (char) url[i++];
		if (c != '/') {
			return notFound(response);
		}

		if (i == length) {
			return handleGetAll(response);
		}

		final List<String> ids = new ArrayList<>();
		final StringBuilder id = new StringBuilder(50);

		while (i < length) {
			if (url[i] == ',') {
				if (id.length() == 0) {
					return badRequest(response);
				}

				ids.add(id.toString());
				id.setLength(0);

				i++;
			} else if (url[i] == '/') {
				if (id.length() == 0) {
					return badRequest(response);
				}

				ids.add(id.toString());
				id.setLength(0);

				break;
			} else {
				id.append((char) url[i++]);
			}
		}

		if (id.length() > 0) {
			ids.add(id.toString());
		}

		return handleGetId(ids, response);
	}

	private HttpResponseStatus handleGetId(final List<String> ids, final ByteBuf response) {
		if (ids.size() == 1) {
			codec.encode(repository.load(ids.get(0)), response);
		} else {
			codec.encode(repository.load(ids), response);
		}

		return OK;
	}

	private HttpResponseStatus handleGetAll(final ByteBuf response) {
		codec.encode(repository.loadAll(), response);
		return OK;
	}

	public HttpResponseStatus handlePost(final byte[] url, final ByteBuf content, final ByteBuf response) {
		final int length = url.length;

		if (resourceLength == length) {
			return handlePostEntity(content, response);
		}

		int i = resourceLength;

		final char c = (char) url[i++];
		if (c != '/') {
			return badRequest(response);
		}

		if (i == length) {
			return handlePostEntity(content, response);
		}

		return badRequest(response);
	}

	private HttpResponseStatus handlePostEntity(final ByteBuf content, final ByteBuf response) {
		final T entity = codec.decode(content);
		codec.encode(of(repository.store(entity)), response);

		return OK;
	}

}
