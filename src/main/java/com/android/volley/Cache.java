/*
 * Copyright (C) 2011 The Android Open Source Project
 *
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
 */

package com.android.volley;

import java.util.Collections;
import java.util.Map;

/**
 * An interface for a cache keyed by a String with a byte array as data.
 */
public interface Cache {
    /**
     * Retrieves an entry from the cache.
     * @param key Cache key
     * @return An {@link Entry} or null in the event of a cache miss
     */
    public Entry get(String key);

    /**
     * Adds or replaces an entry to the cache.
     * @param key Cache key
     * @param entry Data to store and metadata for cache coherency, TTL, etc.
     */
    public void put(String key, Entry entry);

    /**
     * Performs any potentially long-running actions needed to initialize the cache;
     * will be called from a worker thread.
     */
    public void initialize();

    /**
     * Invalidates an entry in the cache.
     * @param key Cache key
     * @param fullExpire True to fully expire the entry, false to soft expire
     */
    public void invalidate(String key, boolean fullExpire);

    /**
     * Removes an entry from the cache.
     * @param key Cache key
     */
    public void remove(String key);

    /**
     * Empties the cache.
     */
    public void clear();

    /**
     * Data and metadata for an entry returned by the cache.
     * 这里放置Entry和map里放置Entry的原理是一样的，都是保存数据，但是为什么不写成接口呢？
     * 因为Entry并没有复杂的数据结构，仅仅是保存数据
     */
    public static class Entry {
        /** The data returned from cache.这个是body */
        public byte[] data;

        /** ETag for cache coherency.http响应首部中用于缓存新鲜度验证的tag */
        public String etag;

        /** Date of this response as reported by the server.http响应首部中的响应时间 */
        public long serverDate;

        /** The last modified date for the requested object. */
        public long lastModified;

        /** TTL for this record.缓存的生存时间 */
        public long ttl;

        /** Soft TTL for this record. 缓存的新鲜时间*/
        public long softTtl;

        /** Immutable response headers as received from server; must be non-null. 响应的headers*/
        public Map<String, String> responseHeaders = Collections.emptyMap();

        /** True if the entry is expired. 判断缓存是否过期*/
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }

        /** True if a refresh is needed from the original data source. 判断缓存是否新鲜，不新鲜的缓存
         * 需要发送到服务端做新鲜度的检测*/
        public boolean refreshNeeded() {
            return this.softTtl < System.currentTimeMillis();
        }
    }

}
