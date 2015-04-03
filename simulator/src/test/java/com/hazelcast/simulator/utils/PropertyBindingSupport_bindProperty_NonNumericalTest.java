package com.hazelcast.simulator.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.hazelcast.simulator.utils.PropertyBindingSupport.bindProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PropertyBindingSupport_bindProperty_NonNumericalTest {

    private final BindPropertyTestClass bindPropertyTestClass = new BindPropertyTestClass();

    @Test
    public void bindProperty_boolean() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "booleanField", "true");
        assertEquals(true, bindPropertyTestClass.booleanField);

        bindProperty(bindPropertyTestClass, "booleanField", "false");
        assertEquals(false, bindPropertyTestClass.booleanField);
    }

    @Test(expected = BindException.class)
    public void bindProperty_boolean_invalid() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "booleanField", "invalid");
    }

    @Test
    public void bindProperty_Boolean() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "booleanObjectField", "null");
        assertNull(bindPropertyTestClass.booleanObjectField);

        bindProperty(bindPropertyTestClass, "booleanObjectField", "true");
        assertEquals(Boolean.TRUE, bindPropertyTestClass.booleanObjectField);

        bindProperty(bindPropertyTestClass, "booleanObjectField", "false");
        assertEquals(Boolean.FALSE, bindPropertyTestClass.booleanObjectField);
    }

    @Test(expected = BindException.class)
    public void bindProperty_Boolean_invalid() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "booleanObjectField", "invalid");
    }

    @Test
    public void bindProperty_class() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "clazz", "null");
        assertNull(bindPropertyTestClass.clazz);

        bindProperty(bindPropertyTestClass, "clazz", ArrayList.class.getName());
        assertEquals(ArrayList.class.getName(), bindPropertyTestClass.clazz.getName());
    }

    @Test(expected = BindException.class)
    public void bindProperty_class_notFound() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "clazz", "com.hazelnuts.simulator.utils.NotFound");
    }

    @Test
    public void bindProperty_string() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "stringField", "null");
        assertNull(bindPropertyTestClass.stringField);

        bindProperty(bindPropertyTestClass, "stringField", "foo");
        assertEquals("foo", bindPropertyTestClass.stringField);
    }

    @Test
    public void bindProperty_enum_nullValue() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "enumField", "null");
        assertNull(bindPropertyTestClass.enumField);
    }

    @Test
    public void bindProperty_enum() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "enumField", TimeUnit.HOURS.name());
        assertEquals(bindPropertyTestClass.enumField, TimeUnit.HOURS);
    }

    @Test
    public void bindProperty_enum_caseInsensitive() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "enumField", "dAyS");
        assertEquals(bindPropertyTestClass.enumField, TimeUnit.DAYS);
    }

    @Test(expected = BindException.class)
    public void bindProperty_enum_notFound() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "enumField", "notExist");
    }

    @Test(expected = BindException.class)
    public void bindProperty_unknownField() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "notExist", "null");
    }

    @Test
    public void bindProperty_withPath() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "otherObject.stringField", "newValue");
        assertEquals("newValue", bindPropertyTestClass.otherObject.stringField);
    }

    @Test(expected = BindException.class)
    public void bindProperty_withPathAndNullValue() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "nullOtherObject.stringField", "newValue");
    }

    @Test(expected = BindException.class)
    public void bindProperty_withPath_missingProperty() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "notExist.stringField", "newValue");
    }

    @Test(expected = BindException.class)
    public void bindProperty_staticField() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass.otherObject, "staticField", "newValue");
    }

    @Test(expected = BindException.class)
    public void bindProperty_finalField() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass.otherObject, "finalField", "newValue");
    }

    @Test(expected = BindException.class)
    public void bindProperty_fallsThroughAllChecks() throws IllegalAccessException {
        bindProperty(bindPropertyTestClass, "comparableField", "newValue");
    }

    @SuppressWarnings("unused")
    private class BindPropertyTestClass {

        private boolean booleanField;
        private Boolean booleanObjectField;

        private Object objectField;
        private String stringField;
        private TimeUnit enumField;
        private Comparable comparableField;

        private OtherObject otherObject = new OtherObject();
        private OtherObject nullOtherObject;
        private Class clazz;
    }

    @SuppressWarnings("unused")
    private static class OtherObject {

        private static Object staticField;

        public final int finalField = 5;

        public String stringField;
    }
}
