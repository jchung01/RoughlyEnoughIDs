package org.dimdev.jeid.proxy;

import java.util.List;

public interface IProxy {
    String NEID = "neid";

    void checkIncompatibleMods();

    List<String> getErrorMessage(String modId);
}
