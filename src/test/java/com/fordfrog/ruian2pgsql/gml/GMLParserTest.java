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
package com.fordfrog.ruian2pgsql.gml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for {@link GMLParser}.
 *
 * @author fordfrog
 */
@RunWith(Parameterized.class)
public class GMLParserTest {

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                    {"point"},
                    {"multipoint"},
                    {"multipoint2"},
                    {"multisurface"},
                    {"multisurface_curves"},
                    {"multisurface_circle"},
                    {"multicurve"}
                });
    }
    private String name;

    public GMLParserTest(final String name) {
        this.name = name;
    }

    @Test
    public void test() {
        final String wkt = convertGMLtoWKT(name + ".xml");
        final String expectedWKT = readWKT(name + ".wkt");

        Assert.assertEquals(expectedWKT, wkt);
    }

    private String convertGMLtoWKT(final String fileName) {
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();

        try (final InputStream inputStream =
                        getClass().getResourceAsStream(fileName)) {
            final XMLStreamReader reader =
                    xMLInputFactory.createXMLStreamReader(inputStream, "UTF-8");

            while (reader.hasNext()) {
                final int event = reader.next();

                if (event == XMLStreamReader.START_ELEMENT) {
                    final Geometry geometry = GMLParser.parse(reader,
                            reader.getNamespaceURI(), reader.getLocalName());

                    return geometry.toWKT();
                }
            }
        } catch (final XMLStreamException ex) {
            throw new RuntimeException("Failed to read XML stream", ex);
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to read input stream", ex);
        }

        return null;
    }

    private String readWKT(final String fileName) {
        try {
            final byte[] bytes = Files.readAllBytes(
                    Paths.get(getClass().getResource(fileName).toURI()));

            return new String(bytes, "UTF-8").trim();
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to read WKT file", ex);
        } catch (final URISyntaxException ex) {
            throw new RuntimeException("Invalid resource URI", ex);
        }
    }
}
