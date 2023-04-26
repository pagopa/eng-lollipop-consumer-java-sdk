/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.model;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class DelayedCacheObject<T> implements Delayed {

    @Getter private final String key;
    @Getter private final SoftReference<T> reference;
    private final long expiryTime;

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(expiryTime, ((DelayedCacheObject) o).expiryTime);
    }
}
