package de.thm.holdem.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassFactoryTest {

    @Test
    void Should_CreateInstance() {
        try {
            ClassFactory<SampleClass> factory = new ClassFactory<>(SampleClass.class);

            SampleClass instance = factory.createInstance(42);

            assertNotNull(instance);
            assertEquals(42, instance.value());

        } catch (ReflectiveOperationException e) {
            fail("Reflection exception occurred: " + e.getMessage());
        }
    }

    public record SampleClass(Integer value) { }
}