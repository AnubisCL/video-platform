package com.example.videoweb.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.builders.*;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.ehcache.expiry.ExpiryPolicy;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Java缓存（封装ehcache）
 * 官网：https://www.ehcache.org/documentation/3.10/getting-started.html
 *
 * @author LinRuChang
 * @version 1.0
 * @date 2022/08/30
 * @since 1.8
 **/
@Slf4j
public class EhcacheUtil {

    protected static final CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build(true);

    /**
     * 默认缓存容器名
     */
    protected static final String DEFAULT_CACHE_NAME = "DEFAULT_CACHE";

    static {
        //默认缓存容器创建
        createCacheIfNot(DEFAULT_CACHE_NAME);
    }


    public static Cache<? super Serializable, ? super Serializable> getCache(String cacheName) {
        return CACHE_MANAGER.getCache(cacheName,Serializable.class,Serializable.class);
    }

    /**
     * 线程安全的创建缓存容器
     * @param cacheName 缓存容器名字
     * @return
     */
    public static Cache<? super Serializable, ? super Serializable> createCache(String cacheName) {
        Assert.notBlank(cacheName, "缓存名缺失");
        Cache<? super Serializable, ? super Serializable> cache = getCache(cacheName);
        if(cache == null) {
            synchronized (EhcacheUtil.class) {
                cache = getCache(cacheName);
                if(cache == null) {
                    //缓存过期策略
                    CustomExpiryPolicy<Object, Object> customExpiryPolicy = new CustomExpiryPolicy<>();
                    //事件监听
                    CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
                            .newEventListenerConfiguration(new CustomCacheEventListener(cacheName), EventType.CREATED, EventType.values())
                            .ordered().asynchronous();

                    return CACHE_MANAGER.createCache(cacheName, CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                    Serializable.class,
                                    Serializable.class,
                                    ResourcePoolsBuilder.heap(Long.MAX_VALUE))
                            .withExpiry(customExpiryPolicy)
                            .withService(cacheEventListenerConfiguration));
                }else {
                    return cache;
                }
            }
        }else {
            return cache;
        }
    }

    /**
     * 如果不存在则创建缓存对象
     *
     * @param cacheName 缓存名
     * @return 缓存对象
     */
    public static Cache<? super Serializable, ? super Serializable> createCacheIfNot(String cacheName) {
        Cache<? super Serializable, ? super Serializable> cache = getCache(cacheName);
        return cache != null ? cache : createCache(cacheName);
    }

    /**
     * 获取键值
     *
     * @param cacheName 缓存容器名 不设置默认使用{@link EhcacheUtil#DEFAULT_CACHE_NAME}
     * @param key       键名
     */
    public static <K extends Serializable> Serializable get(String cacheName, K key) {
        if (key == null) {
            return null;
        }
        cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);
        return (Serializable) (createCacheIfNot(cacheName).get(key));
    }


    /**
     * 获取键值 【从默认缓存容器中取{@link EhcacheUtil#DEFAULT_CACHE_NAME}】
     *
     * @param key 键名
     */
    public static <K extends Serializable> Serializable get(K key) {
        return get(DEFAULT_CACHE_NAME, key);
    }


    public static List<? extends Cache.Entry<? super Serializable, ? super Serializable>> getAllEntry(String cacheName) {
        cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);
        Cache<? super Serializable, ? super Serializable> cache = getCache(cacheName);
        return CollUtil.newArrayList(cache);
    }
    public static List<? extends Cache.Entry<? super Serializable, ? super Serializable>> getAllEntry() {
        return getAllEntry(DEFAULT_CACHE_NAME);
    }


    /**
     * 获取容器{cacheName}所有键值
     * @param cacheName
     * @return
     */
    public static List<? super Serializable> getAllValues(String cacheName) {
        cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);
        return getAllEntry(cacheName).stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
    }

    /**
     * 获取默认容器所有键值
     * @return
     */
    public static List<? super Serializable> getAllValues() {
        return getAllValues(DEFAULT_CACHE_NAME);
    }

    /**
     * 获取容器{cacheName}所有的键名
     * @return
     */
    public static List<? super Serializable> getAllKeys(String cacheName) {
        cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);
        return getAllEntry(cacheName).stream()
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
    }


    /**
     * 获取默认容器所有的键名
     * @return
     */
    public static List<? super Serializable> getAllKeys() {
        return getAllKeys(DEFAULT_CACHE_NAME);
    }


    /**
     * 获取键值 【从默认缓存容器中取{@link EhcacheUtil#DEFAULT_CACHE_NAME}】
     *
     * @param key 键名
     */
    public static <K extends Serializable, V> V getByType(K key, Class<V> resultType) {
        Serializable result = get(DEFAULT_CACHE_NAME, key);
        return Optional.ofNullable(result)
                .map(elem -> Convert.convert(resultType, elem))
                .orElse(null);

    }

    /**
     * 获取键值 【从默认缓存容器中取{@link EhcacheUtil#DEFAULT_CACHE_NAME}】
     *
     * @param key 键名
     */
    public static <K extends Serializable> String getStr(K key) {
        return getByType(key, String.class);
    }


    /**
     * 设置缓存
     *
     * @param cacheName 缓存容器名字，不设置默认使用{@link EhcacheUtil#DEFAULT_CACHE_NAME}
     * @param key       键名
     * @param value     键值
     * @param expireMs  键值对过期时间（单位毫秒）- 不设置、或负数默认为永不过期
     */
    public static <K extends Serializable, V extends Serializable> void set(String cacheName, K key, V value, Long expireMs) {
        cacheName = StrUtil.blankToDefault(cacheName, DEFAULT_CACHE_NAME);

        if (expireMs != null && expireMs >= 0) {
            CacheRuntimeConfiguration runtimeConfiguration = createCacheIfNot(cacheName).getRuntimeConfiguration();
            CustomExpiryPolicy expiryPolicy = (CustomExpiryPolicy) runtimeConfiguration.getExpiryPolicy();
            expiryPolicy.setExpire(key, Duration.ofMillis(expireMs));
        }

        createCacheIfNot(cacheName).put(key, value);
    }

    /**
     * 设置缓存（使用默认的缓存容器{@link EhcacheUtil#DEFAULT_CACHE_NAME）
     *
     * @param key      键名
     * @param value    键值
     * @param expireMs 键值对过期时间（单位毫秒）- 不设置、或负数默认为永不过期
     */
    public static <K extends Serializable, V extends Serializable> void set(String key, V value, Long expireMs) {
        set(DEFAULT_CACHE_NAME, key, value, expireMs);
    }

    /**
     * 设置缓存（使用默认的缓存容器{@link EhcacheUtil#DEFAULT_CACHE_NAME），键值对永不过期
     *
     * @param key   键名
     * @param value 键值
     */
    public static <V extends Serializable> void set(String key, V value) {
        set(key, value, null);
    }



    /**
     * 删除某个容器的某个键值对
     * @param cacheName 容器名
     * @param key 键名
     * @return
     */
    public static Serializable del(String cacheName, Serializable key) {
        cacheName  = StrUtil.blankToDefault(cacheName,DEFAULT_CACHE_NAME);
        Cache<? super Serializable, ? super Serializable> cache = getCache(cacheName);

        return Optional.ofNullable((Serializable)cache.get(key))
                .map(value -> {
                    cache.remove(key);
                    return value;
                })
                .orElse(null);
    }

    /**
     * 删除某人容器的某个键值对
     * @param key 键名
     * @return
     */
    public static Serializable del(Serializable key) {
        return del(DEFAULT_CACHE_NAME,key);
    }

    /**
     * 清空某个缓存容器的内容
     * @param cacheName 缓存容器名
     */
    public static void clear(String cacheName) {
        Cache<? super Serializable, ? super Serializable> cache = getCache(cacheName);
        if(cache != null) {
            cache.clear();
        }
    }

    /**
     * 销毁全部容器
     */
    public static void close() {
        CACHE_MANAGER.close();
    }


    /**
     * keyValue自定义过期时间
     * <p>
     * 参考的实现{@link ExpiryPolicyBuilder#noExpiration(),ExpiryPolicyBuilder#timeToLiveExpiration(Duration),ExpiryPolicyBuilder#timeToIdleExpiration(Duration)}
     * 文档<a href="https://www.ehcache.org/documentation/3.10/expiry.html"></a>
     *
     * @param <K>
     * @param <V>
     */
    private static class CustomExpiryPolicy<K, V> implements ExpiryPolicy<K, V> {

        private final ConcurrentHashMap<K, Duration> keyExpireMap = new ConcurrentHashMap();


        public Duration setExpire(K key, Duration duration) {
            return keyExpireMap.put(key, duration);
        }

        public Duration getExpireByKey(K key) {
            return Optional.ofNullable(keyExpireMap.get(key))
                    .orElse(null);
        }

        public Duration removeExpire(K key) {
            return keyExpireMap.remove(key);
        }


        @Override
        public Duration getExpiryForCreation(K key, V value) {
            return Optional.ofNullable(getExpireByKey(key))
                    .orElse(Duration.ofNanos(Long.MAX_VALUE));
        }


        @Override
        public Duration getExpiryForAccess(K key, Supplier<? extends V> value) {
            return getExpireByKey(key);
        }

        @Override
        public Duration getExpiryForUpdate(K key, Supplier<? extends V> oldValue, V newValue) {
            return getExpireByKey(key);
        }
    }

    /**
     * 自定义事件处理 == 当前主要是用于去除自定义键值对的Map过期时间东西，防止内存溢出
     * <p>
     * 文档<a href="https://www.ehcache.org/documentation/3.10/cache-event-listeners.html"></a>
     *
     * @param <K>
     * @param <V>
     */
    private static class CustomCacheEventListener<K, V> implements CacheEventListener<K, V> {

        String cacheName;

        Cache cache;

        CustomExpiryPolicy customExpiryPolicy;


        public CustomCacheEventListener(String cacheName) {
            this.cacheName = cacheName;
        }


        @Override
        public void onEvent(CacheEvent event) {
            String key = (String) event.getKey();
            Object serializable = EhcacheUtil.get(cacheName, key);
            log.info("事件触发：{}======{}========{}==={}", cacheName, event.getType(), key, JSON.toJSONString(serializable));
            this.cache = ObjectUtil.defaultIfNull(cache, CACHE_MANAGER.getCache(cacheName, Serializable.class, Serializable.class));
            this.customExpiryPolicy = ObjectUtil.defaultIfNull(customExpiryPolicy, (CustomExpiryPolicy) this.cache.getRuntimeConfiguration().getExpiryPolicy());
            if (StrUtil.equalsAnyIgnoreCase(event.getType().name(), EventType.EXPIRED.name(), EventType.EVICTED.name(), EventType.REMOVED.name())) {
                customExpiryPolicy.removeExpire(key);
            }
        }
    }


}


