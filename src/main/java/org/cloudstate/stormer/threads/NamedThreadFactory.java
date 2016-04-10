/**
 */
package org.cloudstate.stormer.threads;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

/**
 */
public final class NamedThreadFactory implements ThreadFactory {

	private static final Logger LOG = getLogger(NamedThreadFactory.class);

	private final String prefix;
	private final AtomicInteger counter;

	private NamedThreadFactory(final String prefix) {
		this.prefix = prefix.trim();
		counter = new AtomicInteger(0);
	}

	public static ThreadFactory threadFactory(final String prefix) {
		return new NamedThreadFactory(prefix);
	}

	/**
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(final Runnable r) {
		final Thread thread = new Thread(r);
		thread.setName(prefix + "-" + counter.getAndIncrement());

		LOG.info("-- newThread() created thread {}", thread.getName());

		return thread;
	}

}
