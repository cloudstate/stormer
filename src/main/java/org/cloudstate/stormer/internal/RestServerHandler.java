/**
 */
package org.cloudstate.stormer.internal;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.nio.charset.Charset.forName;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.charset.Charset;
import java.util.List;

import org.cloudstate.stormer.json.StringConverter;
import org.slf4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 */
@Sharable
public final class RestServerHandler extends SimpleChannelInboundHandler<Object>implements StringConverter {

	private static final Logger LOG = getLogger(RestServerHandler.class);

	private static final Charset UTF_8 = forName("UTF-8");

	private static final String ACCEPT = "Accept";
	private static final String VND_API_JSON = "application/vnd.api+json";
	private static final byte[] WRONG_ACCEPT_HEADER = "No Accept: application/vnd.api+json header".getBytes(UTF_8);

	private final List<ResourceHandler<?>> resourceHandlers;

	public RestServerHandler(final List<ResourceHandler<?>> resourceHandlers) {
		this.resourceHandlers = resourceHandlers;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
		final DecoderResult decoderResult = ((HttpObject) msg).getDecoderResult();
		if (!decoderResult.isSuccess()) {
			LOG.error("Decoder error.");
			return;
		}

		final boolean keepAlive = HttpHeaders.isKeepAlive((HttpRequest) msg);
		final ByteBuf buffer = ctx.alloc().ioBuffer();

		if (msg instanceof HttpRequest) {
			final HttpRequest request = (HttpRequest) msg;

			final HttpHeaders headers = request.headers();
			if (!VND_API_JSON.equals(headers.get(ACCEPT))) {
				buffer.writeBytes(WRONG_ACCEPT_HEADER);
				writeResponse(BAD_REQUEST, ctx, buffer, keepAlive);
				return;
			}

			final String url = request.getUri();
			ResourceHandler<?> resourceHandler = null;

			for (int i = 0; i < resourceHandlers.size(); i++) {
				final ResourceHandler<?> rh = resourceHandlers.get(i);
				if (rh.matches(url)) {
					resourceHandler = rh;
					break;
				}
			}

			if (resourceHandler == null) {
				writeResponse(BAD_REQUEST, ctx, buffer, keepAlive);
				return;
			}

			final HttpMethod method = request.getMethod();
			LOG.info("-- channelRead0() {} {}", method.name(), url);

			if (GET.equals(method)) {
				final HttpResponseStatus status = resourceHandler.handleGet(url.getBytes(), buffer);
				writeResponse(status, ctx, buffer, keepAlive);

				return;
			}

			if (POST.equals(method)) {
				if (msg instanceof HttpContent) {
					final HttpResponseStatus status = resourceHandler.handlePost(url.getBytes(), ((HttpContent) msg).content(), buffer);
					writeResponse(status, ctx, buffer, keepAlive);
				}

				return;
			}

			writeResponse(METHOD_NOT_ALLOWED, ctx, buffer, keepAlive);
		}
	}

	@Override
	public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	private void writeResponse(final HttpResponseStatus status, final ChannelHandlerContext ctx, final ByteBuf content, final boolean keepAlive) {
		final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, content);
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(CONTENT_TYPE, VND_API_JSON);

		if (keepAlive) {
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			ctx.writeAndFlush(response);
		} else {
			ctx.writeAndFlush(response).addListener(CLOSE);
		}
	}

}
