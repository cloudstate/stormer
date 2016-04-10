/**
 */
package org.cloudstate.rest;

import static java.nio.charset.Charset.forName;
import static org.cloudstate.rest.threads.NamedThreadFactory.threadFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;

import org.cloudstate.rest.internal.ResourceHandler;
import org.cloudstate.rest.internal.RestServerHandler;
import org.slf4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;

/**
 */
public final class ServerBuilder {

	private ServerBuilder() {
		throw new UnsupportedOperationException();
	}

	public static Bootstrap server(final String address) {
		return new Server(address);
	}

	public static interface Bootstrap extends BootstrapPort {
		BootstrapPort port(int port);
	}

	public static interface BootstrapPort extends ThreadsBuilder {
		ThreadsBuilder baseUrl(String baseUrl);
	}

	public static interface ThreadsBuilder extends ThreadsBuilderAcceptors {
		ThreadsBuilderAcceptors acceptors(ThreadFactory factory);
	}

	public static interface ThreadsBuilderAcceptors extends ResourceBuilder {
		ResourceBuilder workers(int threads);

		ResourceBuilder workers(ThreadFactory factory);

		ResourceBuilder workers(int threads, ThreadFactory factory);
	}

	public static interface ResourceBuilder {
		<T> ResourceBuilder resource(String name, Repository<T> repository, Codec<T> codec);

		Future<Void> start() throws Exception;
	}

	public static final class Server implements Bootstrap {

		private static final Logger LOG = getLogger(Server.class);

		private static final Charset UTF_8 = forName("UTF-8");

		private static final int DEFAULT_PORT = 80;
		private static final String SLASH = "/";
		private static final String DEFAULT_BASE_URL = SLASH;
		private static final int DEFALUT_ACCEPTOR_THREADS = 1;
		private static final int DEFALUT_WORKER_THREADS = 0; // Magic numer that means the number of available processors times two

		private final byte[] address;
		private int port = DEFAULT_PORT;
		private String baseUrl = DEFAULT_BASE_URL;

		private EventLoopGroup acceptors;
		private EventLoopGroup workers;

		protected final List<ResourceHandler<?>> resourceHandlers;

		protected Server(final String address) {
			this.address = check(address, notBlank(), "address is not valid").getBytes(UTF_8);
			resourceHandlers = new ArrayList<>();
		}

		@Override
		public BootstrapPort port(final int port) {
			this.port = check(port, p -> p > 0 && p < 65536, "port is not valid");
			return this;
		}

		@Override
		public ThreadsBuilder baseUrl(final String baseUrl) {
			this.baseUrl = slash(check(baseUrl, notBlank(), "baseUrl is not valid"));
			return this;
		}

		@Override
		public ThreadsBuilderAcceptors acceptors(final ThreadFactory factory) {
			check(factory, f -> f != null, "acceptors factory is null");
			acceptors = new NioEventLoopGroup(DEFALUT_ACCEPTOR_THREADS, factory);

			return this;
		}

		@Override
		public ResourceBuilder workers(final int threads) {
			check(threads, t -> t > 0, "workers threads is not valid");
			workers = new NioEventLoopGroup(threads);

			return this;
		}

		@Override
		public ResourceBuilder workers(final ThreadFactory factory) {
			check(factory, f -> f != null, "workers factory is null");
			workers = new NioEventLoopGroup(DEFALUT_WORKER_THREADS, factory);

			return null;
		}

		@Override
		public ResourceBuilder workers(final int threads, final ThreadFactory factory) {
			check(threads, t -> t > 0, "workers threads is not valid");
			check(factory, f -> f != null, "workers factory is null");
			workers = new NioEventLoopGroup(threads, factory);

			return this;
		}

		@Override
		public <T> ResourceBuilder resource(final String name, final Repository<T> repository, final Codec<T> codec) {
			final String resourceName = baseUrl.endsWith(SLASH) ? baseUrl + name : baseUrl + slash(name);
			LOG.info("-- resource() registering resource {}", resourceName);

			codec.setAddres(address);
			resourceHandlers.add(ResourceHandler.create(resourceName, repository, codec));

			return this;
		}

		@Override
		public Future<Void> start() throws Exception {
			check(resourceHandlers, rh -> !rh.isEmpty(), "No resources are rigistered");

			if (acceptors == null) {
				acceptors(threadFactory("acceptor"));
			}

			if (workers == null) {
				workers(threadFactory("worker"));
			}

			final ChannelFuture cf = new ServerBootstrap()
					.group(acceptors, workers)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(final SocketChannel ch) throws Exception {
							final ChannelPipeline p = ch.pipeline();
							p.addLast(new HttpRequestDecoder());
							p.addLast(new HttpObjectAggregator(1048576));
							p.addLast(new HttpResponseEncoder());
							p.addLast(new RestServerHandler(resourceHandlers));
						}
					})
					.bind()
					.sync();

			final ChannelFuture result = cf.channel().closeFuture();

			LOG.info("-- start() server is started");

			return result;
		}

		private static Predicate<String> notBlank() {
			return s -> s != null && !s.isEmpty();
		}

		private static <T> T check(final T value, final Predicate<T> predicate, final String message) {
			if (!predicate.test(value)) {
				LOG.error("-- check() {}", message);
				throw new IllegalArgumentException(message);
			}

			return value;
		}

		private static String slash(final String url) {
			if (url == null) {
				return SLASH;
			}

			return url.startsWith(SLASH) ? url : SLASH + url;
		}

	}

}
