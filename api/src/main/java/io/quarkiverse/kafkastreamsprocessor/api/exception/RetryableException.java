/*-
 * #%L
 * Quarkus Kafka Streams Processor
 * %%
 * Copyright (C) 2024 Amadeus s.a.s.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.quarkiverse.kafkastreamsprocessor.api.exception;

/**
 * Generic {@link RuntimeException} use by the RetryDecorator to specify that a message should be processed again.
 */
public class RetryableException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public RetryableException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public RetryableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public RetryableException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    protected RetryableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
