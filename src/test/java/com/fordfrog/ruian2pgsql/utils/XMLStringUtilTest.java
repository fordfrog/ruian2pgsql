/**
 * Copyright 2012 Miroslav Šulc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fordfrog.ruian2pgsql.utils;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link XMLStringUtil}.
 *
 * @author fordfrog
 */
public class XMLStringUtilTest {

    /**
     * Original GML with pointMembers and single point.
     */
    private static final String MULTIPOINT_ORIGINAL1 =
            "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
            + "gml:id=\"DOB.545058.X\" srsName=\"urn:ogc:def:crs:EPSG::2065\" "
            + "srsDimension=\"2\"><gml:pointMembers>"
            + "<gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "</gml:pointMembers></gml:MultiPoint>";
    /**
     * Adjusted original 1 GML with pointMember instead of pointMembers.
     */
    private static final String MULTIPOINT_RESULT1 =
            "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
            + "gml:id=\"DOB.545058.X\" srsName=\"urn:ogc:def:crs:EPSG::2065\" "
            + "srsDimension=\"2\"><gml:pointMember>"
            + "<gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "</gml:pointMember></gml:MultiPoint>";
    /**
     * Original GML with pointMembers and multiple points.
     */
    private static final String MULTIPOINT_ORIGINAL2 =
            "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
            + "gml:id=\"DOB.545058.X\" srsName=\"urn:ogc:def:crs:EPSG::2065\" "
            + "srsDimension=\"2\"><gml:pointMembers>"
            + "<gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "<gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "</gml:pointMembers></gml:MultiPoint>";
    /**
     * Adjusted original 2 GML with pointMember instead of pointMembers.
     */
    private static final String MULTIPOINT_RESULT2 =
            "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
            + "gml:id=\"DOB.545058.X\" srsName=\"urn:ogc:def:crs:EPSG::2065\" "
            + "srsDimension=\"2\"><gml:pointMember>"
            + "<gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "</gml:pointMember>"
            + "<gml:pointMember><gml:Point gml:id=\"DOB.545058.1\">"
            + "<gml:pos>496547.00 1139895.00</gml:pos></gml:Point>"
            + "</gml:pointMember></gml:MultiPoint>";

    /**
     * Tests reading GML without any modification.
     *
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     */
    @Test
    public void testNoWorkaround() throws UnsupportedEncodingException,
            XMLStreamException {
        testGML(MULTIPOINT_ORIGINAL1, MULTIPOINT_ORIGINAL1, false);
    }

    /**
     * Tests reading multipoint GML with one point with multipoint bug
     * workaround enabled.
     *
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     */
    @Test
    public void testMultipointWorkaroundSinglePoint()
            throws UnsupportedEncodingException, XMLStreamException {
        testGML(MULTIPOINT_ORIGINAL1, MULTIPOINT_RESULT1, true);
    }

    /**
     * Tests reading multipoint GML with multiple points with multipoint bug
     * workaround enabled.
     *
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     */
    @Test
    public void testMultipointWorkaroundMultiPoints()
            throws UnsupportedEncodingException, XMLStreamException {
        testGML(MULTIPOINT_ORIGINAL2, MULTIPOINT_RESULT2, true);
    }

    /**
     * Tests GML reading.
     *
     * @param input                   input GML
     * @param expResult               expected GML result
     * @param multipointBugWorkaround whether multipoint bug workaround should
     *                                be enabled
     *
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     */
    private void testGML(final String input, final String expResult,
            final boolean multipointBugWorkaround)
            throws UnsupportedEncodingException, XMLStreamException {
        XMLStringUtil.setMultipointBugWorkaround(multipointBugWorkaround);

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(input.getBytes("UTF-8"));
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();
        final XMLStreamReader reader =
                xMLInputFactory.createXMLStreamReader(inputStream);
        int event;

        do {
            event = reader.next();
        } while (event != XMLStreamReader.START_ELEMENT);

        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        final String result = XMLStringUtil.createGMLString(
                reader, null, new OutputStreamWriter(System.out));

        Assert.assertThat(result, IsEqual.equalTo(expResult));
    }
}
