/*
 * $HeadURL$
 * $Revision$
 * $Date$
 * 
 * ====================================================================
 *
 *  Copyright 1999-2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.util;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.io.HttpDataReceiver;
import org.apache.http.mockup.HttpDataReceiverMockup;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit tests for {@link Header}.
 *
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 */
public class TestHeaderUtils extends TestCase {

    public TestHeaderUtils(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestHeaderUtils.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite() {
        return new TestSuite(TestHeaderUtils.class);
    }

    public void testInvalidInput() throws Exception {
        try {
            HeaderUtils.parseHeaders(null);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    public void testBasicHeaderParsing() throws Exception {
        String s = 
            "header1: stuff\r\n" + 
            "header2  : stuff \r\n" + 
            "header3: stuff\r\n" + 
            "     and more stuff\r\n" + 
            "\t and even more stuff\r\n" +  
            "     \r\n" +  
            "\r\n"; 
        HttpDataReceiver receiver = new HttpDataReceiverMockup(s, "US-ASCII"); 
        Header[] headers = HeaderUtils.parseHeaders(receiver);
        assertNotNull(headers);
        assertEquals(3, headers.length);
        assertEquals("header1", headers[0].getName());
        assertEquals("stuff", headers[0].getValue());
        assertEquals("header2", headers[1].getName());
        assertEquals("stuff", headers[1].getValue());
        assertEquals("header3", headers[2].getName());
        assertEquals("stuff and more stuff and even more stuff", headers[2].getValue());
    }

    public void testBufferedHeader() throws Exception {
        String s = 
            "header1  : stuff; param1 = value1; param2 = \"value 2\" \r\n" + 
            "\r\n"; 
        HttpDataReceiver receiver = new HttpDataReceiverMockup(s, "US-ASCII"); 
        Header[] headers = HeaderUtils.parseHeaders(receiver);
        assertNotNull(headers);
        assertEquals(1, headers.length);
        assertEquals("header1  : stuff; param1 = value1; param2 = \"value 2\" ", headers[0].toString());
        HeaderElement[] elements = headers[0].getElements();
        assertNotNull(elements);
        assertEquals(1, elements.length);
        assertEquals("stuff", elements[0].getName());
        assertEquals(null, elements[0].getValue());
        NameValuePair[] params = elements[0].getParameters();
        assertNotNull(params);
        assertEquals(2, params.length);
        assertEquals("param1", params[0].getName());
        assertEquals("value1", params[0].getValue());
        assertEquals("param2", params[1].getName());
        assertEquals("value 2", params[1].getValue());
    }

    public void testParsingInvalidHeaders() throws Exception {
        String s = "    stuff\r\n" + 
            "header1: stuff\r\n" + 
            "\r\n"; 
        HttpDataReceiver receiver = new HttpDataReceiverMockup(s, "US-ASCII");
        try {
            HeaderUtils.parseHeaders(receiver);
            fail("ProtocolException should have been thrown");
        } catch (ProtocolException ex) {
            // expected
        }
        s = "  :  stuff\r\n" + 
            "header1: stuff\r\n" + 
            "\r\n"; 
        receiver = new HttpDataReceiverMockup(s, "US-ASCII");
        try {
            HeaderUtils.parseHeaders(receiver);
            fail("ProtocolException should have been thrown");
        } catch (ProtocolException ex) {
            // expected
        }
    }
    
    public void testParsingMalformedFirstHeader() throws Exception {
        String s = 
            "    header1: stuff\r\n" + 
            "header2  : stuff \r\n"; 
        HttpDataReceiver receiver = new HttpDataReceiverMockup(s, "US-ASCII"); 
        Header[] headers = HeaderUtils.parseHeaders(receiver);
        assertNotNull(headers);
        assertEquals(2, headers.length);
        assertEquals("header1", headers[0].getName());
        assertEquals("stuff", headers[0].getValue());
        assertEquals("header2", headers[1].getName());
        assertEquals("stuff", headers[1].getValue());
    }
    
    public void testEmptyDataStream() throws Exception {
        String s = ""; 
        HttpDataReceiver receiver = new HttpDataReceiverMockup(s, "US-ASCII"); 
        Header[] headers = HeaderUtils.parseHeaders(receiver);
        assertNotNull(headers);
        assertEquals(0, headers.length);
    }

}

