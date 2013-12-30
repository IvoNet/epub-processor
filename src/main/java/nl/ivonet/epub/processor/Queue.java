/*
 * Copyright (c) 2013 Ivo Woltring
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.ivonet.epub.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Ivo Woltring
 */
public class Queue<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);

    private final BlockingQueue<T> queue;

    public Queue() {
        this.queue = new ArrayBlockingQueue<>(50);
    }

    public void put(final T data) {
        LOG.info("Putting {} on queue", data);
        while (!this.queue
                .offer(data)) {
            LOG.warn("Queue is full. Trying again later.");
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

    private void logerror(final Exception e) {
        LOG.error("[ERROR] occurred in class {} with message {}.", getClass(), e.getMessage());
    }

    public T get() throws InterruptedException {
        return this.queue
                .take();
    }

    public int size() {
        return queue.size();
    }

}
