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

import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.text.MessageFormat;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * GML parser.
 *
 * @author fordfrog
 */
public class GMLParser {

    /**
     * Parses GML string into geometry object.
     *
     * @param reader       XML stream reader
     * @param endNamespace namespace where to end reading
     * @param endLocalName local name where to end reading
     *
     * @return parsed geometry
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Geometry parse(final XMLStreamReader reader,
            final String endNamespace, final String endLocalName)
            throws XMLStreamException {
        if (Namespaces.GML.equals(endNamespace)) {
            return parseGML(reader);
        }

        Geometry geometry = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (Namespaces.GML.equals(reader.getNamespaceURI())) {
                        geometry = parseGML(reader);
                    } else {
                        throwUnexpectedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            endNamespace, endLocalName, reader)) {
                        return geometry;
                    } else {
                        throwUnexpectedElement(reader);
                    }

                    break;
            }
        }

        return geometry;
    }

    /**
     * Parses GML top element.
     *
     * @param reader XML stream reader
     *
     * @return parsed geometry
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Geometry parseGML(final XMLStreamReader reader)
            throws XMLStreamException {
        switch (reader.getLocalName()) {
            case "MultiPoint":
                return parseMultiPoint(reader);
            case "MultiSurface":
                return parseMultiSurface(reader);
            case "MultiCurve":
                return parseMultiCurve(reader);
            case "Point":
                return parsePoint(reader);
            case "Polygon":
                return parsePolygon(reader);
            default:
                throwUnsupportedElement(reader);
                return null;
        }
    }

    /**
     * Parses multipoint.
     *
     * @param reader XML stream reader
     *
     * @return parsed multipoint
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static MultiPoint parseMultiPoint(final XMLStreamReader reader)
            throws XMLStreamException {
        final MultiPoint multiPoint = new MultiPoint();
        multiPoint.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "pointMembers", reader)) {
                        parsePointMembers(reader, multiPoint);
                    } else {
                        throwUnsupportedElement(reader);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "MultiPoint", reader)) {
                        return multiPoint;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return multiPoint;
    }

    /**
     * Parses multisurface.
     *
     * @param reader XML stream reader
     *
     * @return parsed multipolygon
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static MultiSurface parseMultiSurface(final XMLStreamReader reader)
            throws XMLStreamException {
        final MultiSurface multiPolygon = new MultiSurface();
        multiPolygon.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "surfaceMember", reader)) {
                        parseSurfaceMember(reader, multiPolygon);
                    } else {
                        throwUnsupportedElement(reader);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "MultiSurface", reader)) {
                        return multiPolygon;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return multiPolygon;
    }

    /**
     * Parses multicurve.
     *
     * @param reader XML stream reader
     *
     * @return parsed multicurve
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static MultiCurve parseMultiCurve(final XMLStreamReader reader)
            throws XMLStreamException {
        final MultiCurve multiCurve = new MultiCurve();
        multiCurve.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "curveMember", reader)) {
                        multiCurve.addSegment(parseCurveMember(reader));
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "MultiCurve", reader)) {
                        return multiCurve;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return multiCurve;
    }

    /**
     * Reads SRID from current element.
     *
     * @param reader XML stream reader
     *
     * @return parsed SRID or null if SRID is not present
     */
    private static Integer getSrid(final XMLStreamReader reader) {
        final String value = reader.getAttributeValue(null, "srsName");

        if (value == null || value.isEmpty()) {
            return null;
        }

        switch (value) {
            case "urn:ogc:def:crs:EPSG::2065":
                return 2065;
            default:
                throw new RuntimeException(
                        MessageFormat.format("Unsupported SRID {0}", value));
        }
    }

    /**
     * Parses point members.
     *
     * @param reader     XML stream reader
     * @param multiPoint parent multipoint
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void parsePointMembers(final XMLStreamReader reader,
            final MultiPoint multiPoint) throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Point", reader)) {
                        multiPoint.addPoint(parsePoint(reader));
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "pointMembers", reader)) {
                        return;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }
    }

    /**
     * Parses surface member.
     *
     * @param reader       XML stream reader
     * @param multiPolygon parent multipolygon
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void parseSurfaceMember(final XMLStreamReader reader,
            final MultiSurface multiPolygon) throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Polygon", reader)) {
                        multiPolygon.addPolygon(parsePolygon(reader));
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "surfaceMember", reader)) {
                        return;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }
    }

    /**
     * Parses point.
     *
     * @param reader XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Point parsePoint(final XMLStreamReader reader)
            throws XMLStreamException {
        final Point point = new Point();
        point.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(Namespaces.GML, "pos", reader)) {
                        parsePos(reader, point);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Point", reader)) {
                        return point;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return point;
    }

    /**
     * Parses polygon.
     *
     * @param reader XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Polygon parsePolygon(final XMLStreamReader reader)
            throws XMLStreamException {
        final Polygon polygon = new Polygon();
        polygon.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "exterior", reader)) {
                        polygon.setOuter(parsePolygonPart(reader));
                    } else if (XMLUtils.isSameElement(
                            Namespaces.GML, "interior", reader)) {
                        polygon.addInner(parsePolygonPart(reader));
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Polygon", reader)) {
                        return polygon;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return polygon;
    }

    /**
     * Parses polygon exterior/interior ring.
     *
     * @param reader XML stream reader
     *
     * @return parsed line string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Geometry parsePolygonPart(final XMLStreamReader reader)
            throws XMLStreamException {
        final String endLocalName = reader.getLocalName();
        Geometry geometry = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "LinearRing", reader)) {
                        geometry = parseLinearRing(reader, "LinearRing");
                    } else if (XMLUtils.isSameElement(
                            Namespaces.GML, "Ring", reader)) {
                        geometry = parseRing(reader);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, endLocalName, reader)) {
                        return geometry;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return geometry;
    }

    /**
     * Parses LinearRing.
     *
     * @param reader       XML stream reader
     * @param endLocalName end local name
     *
     * @return parsed line string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static LineString parseLinearRing(final XMLStreamReader reader,
            final String endLocalName) throws XMLStreamException {
        final LineString lineString = new LineString();
        lineString.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "posList", reader)) {
                        parsePosList(reader, lineString);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, endLocalName, reader)) {
                        return lineString;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return lineString;
    }

    /**
     * Parses Ring.
     *
     * @param reader XML stream reader
     *
     * @return parsed ring
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static CompoundCurve parseRing(final XMLStreamReader reader)
            throws XMLStreamException {
        final CompoundCurve compoundCurve = new CompoundCurve();
        compoundCurve.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "curveMember", reader)) {
                        compoundCurve.addSegment(parseCurveMember(reader));
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Ring", reader)) {
                        return compoundCurve;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return compoundCurve;
    }

    /**
     * Parses curveMember.
     *
     * @param reader XML stream reader
     *
     * @return parsed geometry
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Geometry parseCurveMember(final XMLStreamReader reader)
            throws XMLStreamException {
        Geometry geometry = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "LineString", reader)) {
                        geometry = parseLinearRing(reader, "LineString");
                    } else if (XMLUtils.isSameElement(
                            Namespaces.GML, "Curve", reader)) {
                        geometry = parseCurve(reader);
                    } else {
                        throwUnsupportedElement(reader);
                    }
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "curveMember", reader)) {
                        return geometry;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return geometry;
    }

    /**
     * Parses Curve.
     *
     * @param reader XML stream reader
     *
     * @return parsed geometry
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Geometry parseCurve(final XMLStreamReader reader)
            throws XMLStreamException {
        Geometry geometry = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "segments", reader)) {
                        geometry = parseSegments(reader);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Curve", reader)) {
                        return geometry;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return geometry;
    }

    /**
     * Parses curve segments.
     *
     * @param reader XML stream reader
     *
     * @return parsed geometry
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Geometry parseSegments(final XMLStreamReader reader)
            throws XMLStreamException {
        Geometry geometry = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (geometry != null) {
                        throw new RuntimeException(
                                "Multiple Curve segments are not supported");
                    }

                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "ArcString", reader)) {
                        geometry = parseArcString(reader);
                    } else if (XMLUtils.isSameElement(
                            Namespaces.GML, "Circle", reader)) {
                        geometry = parseCircle(reader);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "segments", reader)) {
                        return geometry;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return geometry;
    }

    /**
     * Parses ArcString.
     *
     * @param reader XML stream reader
     *
     * @return parsed circular string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static CircularString parseArcString(final XMLStreamReader reader)
            throws XMLStreamException {
        CircularString circularString = new CircularString();
        circularString.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "posList", reader)) {
                        parsePosList(reader, circularString);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "ArcString", reader)) {
                        return circularString;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return circularString;
    }

    /**
     * Parses Circle.
     *
     * @param reader XML stream reader
     *
     * @return parsed circle
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Circle parseCircle(final XMLStreamReader reader)
            throws XMLStreamException {
        Circle circle = new Circle();
        circle.setSrid(getSrid(reader));

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "posList", reader)) {
                        parsePosList(reader, circle);
                    } else {
                        throwUnsupportedElement(reader);
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            Namespaces.GML, "Circle", reader)) {
                        return circle;
                    } else {
                        throwUnexpectedElement(reader);
                    }
            }
        }

        return circle;
    }

    /**
     * Parses position.
     *
     * @param reader XML stream reader
     * @param point  parent point
     *
     * @throws XMLStreamException Thrown if problem occurred while parsing XML
     *                            stream.
     */
    private static void parsePos(final XMLStreamReader reader,
            final Point point) throws XMLStreamException {
        final String[] parts = reader.getElementText().split(" ");
        point.setX(Double.parseDouble(parts[0]));
        point.setY(Double.parseDouble(parts[1]));
    }

    /**
     * Parses positions list.
     *
     * @param reader   XML stream reader
     * @param geometry parent geometry
     *
     * @return parsed line string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void parsePosList(final XMLStreamReader reader,
            final GeometryWithPoints geometry) throws XMLStreamException {
        final String[] parts = reader.getElementText().split(" ");

        for (int i = 0; i < parts.length; i += 2) {
            geometry.addPoint(new Point(Double.parseDouble(parts[i]),
                    Double.parseDouble(parts[i + 1])));
        }
    }

    /**
     * Throws exception with unsupported element information.
     *
     * @param reader XML stream reader
     */
    private static void throwUnsupportedElement(final XMLStreamReader reader) {
        throw new RuntimeException(MessageFormat.format(
                "Unsupported element {0}:{1}", reader.getNamespaceURI(),
                reader.getLocalName()));
    }

    /**
     * Throws exception with unexpected element information.
     *
     * @param reader XML stream reader
     */
    private static void throwUnexpectedElement(final XMLStreamReader reader) {
        throw new RuntimeException(MessageFormat.format(
                "Unexpected element {0}:{1}", reader.getNamespaceURI(),
                reader.getLocalName()));
    }

    /**
     * Creates new instance of GMLParser.
     */
    private GMLParser() {
    }
}
