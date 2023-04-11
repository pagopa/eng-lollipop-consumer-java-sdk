/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion.storage;

/** Interface for the provider used to create instances of {@link AssertionStorage} */
public interface AssertionStorageProvider {

    /**
     * @return instance of {@link AssertionStorage}
     */
    AssertionStorage provideStorage(StorageConfig storageConfig);
}
