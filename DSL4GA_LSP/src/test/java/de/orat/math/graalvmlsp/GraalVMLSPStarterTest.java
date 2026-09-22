package de.orat.math.graalvmlsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class GraalVMLSPStarterTest {

    @Test
    void acceptsValidPort() {
        assertEquals(8123, GraalVMLSPStarter.parsePort(new String[]{"8123"}));
    }

    @Test
    void rejectsMissingOrAdditionalPorts() {
        assertThrows(IllegalArgumentException.class, () -> GraalVMLSPStarter.parsePort(new String[0]));
        assertThrows(IllegalArgumentException.class,
                () -> GraalVMLSPStarter.parsePort(new String[]{"1", "2"}));
    }

    @Test
    void rejectsNonNumericAndOutOfRangePorts() {
        assertThrows(IllegalArgumentException.class,
                () -> GraalVMLSPStarter.parsePort(new String[]{"not-a-port"}));
        assertThrows(IllegalArgumentException.class, () -> GraalVMLSPStarter.parsePort(new String[]{"0"}));
        assertThrows(IllegalArgumentException.class,
                () -> GraalVMLSPStarter.parsePort(new String[]{"65536"}));
    }
}
