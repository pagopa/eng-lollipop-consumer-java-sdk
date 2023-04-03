package it.pagopa.spid.common.lc.assertion.storage;

import it.pagopa.spid.common.lc.assertion.AssertionService;

public interface AssertionStorageProvider {

    AssertionStorage provideStorage();
}
