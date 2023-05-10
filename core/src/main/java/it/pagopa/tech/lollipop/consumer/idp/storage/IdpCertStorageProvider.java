/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.idp.storage;

/** Interface for the provider used to create instances of {@link IdpCertStorage} */
public interface IdpCertStorageProvider {

    /**
     * @return instance of {@link IdpCertStorage}
     */
    IdpCertStorage provideStorage(IdpCertStorageConfig storageConfig);
}
