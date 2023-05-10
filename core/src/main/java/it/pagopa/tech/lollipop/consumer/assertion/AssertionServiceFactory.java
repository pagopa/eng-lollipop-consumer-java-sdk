/* (C)2023 */
package it.pagopa.tech.lollipop.consumer.assertion;

/** Interface for the factory used to create instances of {@link AssertionServiceFactory} */
public interface AssertionServiceFactory {

    /**
     * @return instance of AssertionService
     */
    AssertionService create();
}
