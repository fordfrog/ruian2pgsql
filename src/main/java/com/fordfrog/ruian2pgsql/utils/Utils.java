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

import com.fordfrog.ruian2pgsql.containers.ItemWithDefinicniBod;
import com.fordfrog.ruian2pgsql.containers.ItemWithMluvCharPad;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.postgresql.geometric.PGpoint;

/**
 * Utilities.
 *
 * @author fordfrog
 */
public class Utils {

    /**
     * Pattern for parsing timestamp value from string.
     */
    private static final Pattern PATTERN_TIMESTAMP = Pattern.compile(
            "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})$");

    /**
     * Creates new instance of Utils.
     */
    private Utils() {
    }

    /**
     * Parses timestamp value from string.
     *
     * @param value string value
     *
     * @return parsed timestamp value
     */
    public static Date parseTimestamp(final String value) {
        final Matcher matcher = PATTERN_TIMESTAMP.matcher(value);

        if (!matcher.matches()) {
            throw new RuntimeException("Invalid timestamp value: " + value);
        }

        final Calendar calendar =
                Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)) - 1,
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4)),
                Integer.parseInt(matcher.group(5)),
                Integer.parseInt(matcher.group(6)));

        return calendar.getTime();
    }

    /**
     * Prints warning about element being ignored into writer.
     *
     * @param logFile log file writer
     * @param reader  XML stream reader
     */
    public static void printWarningIgnoringElement(final Writer logFile,
            final XMLStreamReader reader) {
        try {
            logFile.write("Warning: Ingoring element "
                    + reader.getNamespaceURI() + ' ' + reader.getLocalName()
                    + '\n');
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to write to log file", ex);
        }
    }

    /**
     * Prints text to log writer.
     *
     * @param logFile log file writer
     * @param text    text
     */
    public static void printToLog(final Writer logFile, final String text) {
        try {
            logFile.write(text);
            logFile.write('\n');
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to write to log file", ex);
        }
    }

    /**
     * Checks whether XML stream reader namespace and local name match the one
     * specified in the call.
     *
     * @param namespace namespace
     * @param localName local name
     * @param reader    XML stream reader
     *
     * @return trhe if namespace and local name match, otherwise false
     */
    public static boolean isEndElement(final String namespace,
            final String localName, final XMLStreamReader reader) {
        return namespace.equals(reader.getNamespaceURI())
                && localName.equals(reader.getLocalName());
    }

    /**
     * Processes DefinicniBod element.
     *
     * @param reader       XML stream reader
     * @param endNamespace namespace of DefinicniBod element
     * @param engLocalName local name of DefinicniBod element
     * @param logFile      log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static PGpoint processDefinicniBod(final XMLStreamReader reader,
            final String endNamespace, final String engLocalName,
            final Writer logFile) throws XMLStreamException {
        PGpoint definicniBod = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    definicniBod = processDefinicniBodElement(reader, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(endNamespace, engLocalName, reader)) {
                        return definicniBod;
                    }
            }
        }

        return definicniBod;
    }

    /**
     * Processes sub-elements of DefinicniBod element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static PGpoint processDefinicniBodElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        PGpoint point = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.ADR_MISTO_INT_TYPY:
                switch (reader.getLocalName()) {
                    case "AdresniBod":
                        point = processAdresniBod(reader, logFile);
                        break;
                    default:
                        Utils.printWarningIgnoringElement(logFile, reader);
                }
                break;
            case Namespaces.GML:
                switch (reader.getLocalName()) {
                    case "MultiPoint":
                        final List<PGpoint> list =
                                processMultiPoint(reader, logFile);

                        if (list.size() > 1) {
                            printToLog(logFile, "Warning: definition point "
                                    + "contains more than one point, using "
                                    + "first one.");
                        }

                        if (!list.isEmpty()) {
                            point = list.get(0);
                        }

                        break;
                    case "Point":
                        point = processPoint(reader, logFile);
                        break;
                    default:
                        printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }

        return point;
    }

    /**
     * Processes MultiPoint element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return list of parsed points
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static List<PGpoint> processMultiPoint(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        final List<PGpoint> list = new ArrayList<>(10);

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    list.addAll(processMultiPointElement(reader, logFile));
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(Namespaces.GML, "MultiPoint", reader)) {
                        return list;
                    }
            }
        }

        return list;
    }

    /**
     * Processes sub-elements of MultiPoint element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return list of parsed points
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static List<PGpoint> processMultiPointElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        final List<PGpoint> list = new ArrayList<>(10);

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                switch (reader.getLocalName()) {
                    case "pointMembers":
                        list.addAll(processPointMembers(reader, logFile));
                        break;
                    default:
                        printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }

        return list;
    }

    /**
     * Processes pointMembers element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return list of parsed points
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static List<PGpoint> processPointMembers(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        final List<PGpoint> list = new ArrayList<>(10);

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    list.addAll(processPointMembersElement(reader, logFile));
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(Namespaces.GML, "pointMembers", reader)) {
                        return list;
                    }
            }
        }

        return list;
    }

    /**
     * Processes sub-elements of pointMembers element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return list of parsed points
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static List<PGpoint> processPointMembersElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        final List<PGpoint> list = new ArrayList<>(10);
        int pointsCount = 0;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                switch (reader.getLocalName()) {
                    case "Point":
                        list.add(processPoint(reader, logFile));
                        pointsCount++;
                        break;
                    default:
                        printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }

        return list;
    }

    /**
     * Processes AdresniBod element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static PGpoint processAdresniBod(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        PGpoint point = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    point = processAdresniBodElement(reader, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(Namespaces.ADR_MISTO_INT_TYPY,
                            "AdresniBod", reader)) {
                        return point;
                    }
            }
        }

        return point;
    }

    /**
     * Processes sub-elements of AdresniBod element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static PGpoint processAdresniBodElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        PGpoint point = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                switch (reader.getLocalName()) {
                    case "Point":
                        point = processPoint(reader, logFile);
                        break;
                    default:
                        printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }

        return point;
    }

    /**
     * Processes Point element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static PGpoint processPoint(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        PGpoint point = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    point = processPointElement(reader, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(Namespaces.GML, "Point", reader)) {
                        return point;
                    }
            }
        }

        return point;
    }

    /**
     * Processes sub-elements of Point element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return parsed point
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static PGpoint processPointElement(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        PGpoint point = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                switch (reader.getLocalName()) {
                    case "pos":
                        point = parsePoint(reader.getElementText());
                        break;
                    default:
                        printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }

        return point;
    }

    /**
     * Parses point value into point object.
     *
     * @param value string value
     *
     * @return point object
     */
    private static PGpoint parsePoint(final String value) {
        final String[] parts = value.split(" ");

        return new PGpoint(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]));
    }

    /**
     * Reads Kod of AdresniMisto from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getAdresniMistoKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.BASE_TYPY, "Kod",
                endNamespace, "AdresniMistoKod", logFile).intValue();
    }

    /**
     * Reads Kod of CastObce from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getCastObceKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.CAST_OBCE_INT_TYPY, "Kod",
                endNamespace, "CastObce", logFile).intValue();
    }

    /**
     * Reads Kod of IdentifikacniParcela from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Long getIdentifikacniParcelaId(
            final XMLStreamReader reader, final String endNamespace,
            final Writer logFile) throws XMLStreamException {
        return getElementValue(reader, Namespaces.PARCELA_INT_TYPY, "Id",
                endNamespace, "IdentifikacniParcela", logFile);
    }

    /**
     * Reads Kod of KatastralniUzemi from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getKatastralniUzemiKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.KAT_UZ_INT_TYPY, "Kod",
                endNamespace, "KatastralniUzemi", logFile).intValue();
    }

    /**
     * Reads Kod of Kraj from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getKrajKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.KRAJ_INT_TYPY, "Kod",
                endNamespace, "Kraj", logFile).intValue();
    }

    /**
     * Reads Kod of Momc from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getMomcKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.MOMC_INT_TYPY, "Kod",
                endNamespace, "Momc", logFile).intValue();
    }

    /**
     * Reads Kod of Mop from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getMopKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.MOP_INT_TYPY, "Kod",
                endNamespace, "Mop", logFile).intValue();
    }

    /**
     * Reads Kod of Obec from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getObecKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.OBEC_INT_TYPY, "Kod",
                endNamespace, "Obec", logFile).intValue();
    }

    /**
     * Reads Kod of Okres from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getOkresKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.OKRES_INT_TYPY, "Kod",
                endNamespace, "Okres", logFile).intValue();
    }

    /**
     * Reads Kod of Orp from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getOrpKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.ORP_INT_TYPY, "Kod",
                endNamespace, "Orp", logFile).intValue();
    }

    /**
     * Reads Kod of Pou from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getPouKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.POU_INT_TYPY, "Kod",
                endNamespace, "Pou", logFile).intValue();
    }

    /**
     * Reads Kod of RegionSoudrznosti from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getRegionSoudrznostiKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.REG_SOU_INTI_TYPY, "Kod",
                endNamespace, "RegionSoudrznosti", logFile).intValue();
    }

    /**
     * Reads Kod of SpravniObvod from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getSpravniObvodKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.SPRAV_OBV_INT_TYPY, "Kod",
                endNamespace, "SpravniObvod", logFile).intValue();
    }

    /**
     * Reads Kod of Stat from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getStatKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.STAT_INT_TYPY, "Kod",
                endNamespace, "Stat", logFile).intValue();
    }

    /**
     * Reads Kod of StavebniObjekt from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getStavebniObjektKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.STAV_OBJ_INT_TYPY, "Kod",
                endNamespace, "StavebniObjekt", logFile).intValue();
    }

    /**
     * Reads Kod of Ulice from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getUliceKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.ULICE_INT_TYPY, "Kod",
                endNamespace, "Ulice", logFile).intValue();
    }

    /**
     * Reads Kod of Vusc from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     * @param logFile      log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getVuscKod(final XMLStreamReader reader,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        return getElementValue(reader, Namespaces.VUSC_INT_TYPY, "Kod",
                endNamespace, "Vusc", logFile).intValue();
    }

    /**
     * Reads value from specified element.
     *
     * @param reader         XML stream reader
     * @param valueNamespace namespace of the value element
     * @param valueLocalName local name of the value element
     * @param endNamespace   end namespace of current element
     * @param endElement     local name of current element
     * @param logFile        log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Long getElementValue(final XMLStreamReader reader,
            final String valueNamespace, final String valueLocalName,
            final String endNamespace, final String endElement,
            final Writer logFile) throws XMLStreamException {
        Long kod = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    kod = getElementValueFromElement(
                            reader, valueNamespace, valueLocalName, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (Utils.isEndElement(endNamespace, endElement, reader)) {
                        return kod;
                    }
            }
        }

        return kod;
    }

    /**
     * Reads value from element.
     *
     * @param reader    XML stream reader
     * @param namespace namespace of the value element
     * @param localName local name of the value element
     * @param logFile   log file writer
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Long getElementValueFromElement(
            final XMLStreamReader reader, final String namespace,
            final String localName, final Writer logFile)
            throws XMLStreamException {
        if (namespace.equals(reader.getNamespaceURI())
                && localName.equals(reader.getLocalName())) {
            return Long.valueOf(reader.getElementText());
        } else {
            printWarningIgnoringElement(logFile, reader);
        }

        return null;
    }

    /**
     * Processes Geometrie element. If item supports
     * {@link ItemWithDefinicniBod} interface and Geometrie element contains
     * DefinicniBod element, then DefinicniBod value is set to the item.
     *
     * @param reader    XML stream reader
     * @param item      item
     * @param namespace namespace of Geometrie element
     * @param logFile   log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processGeometrie(final XMLStreamReader reader,
            final Object item, final String namespace, final Writer logFile)
            throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processGeometrieElement(reader, item, namespace, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(namespace, "Geometrie", reader)) {
                        return;
                    }
            }
        }
    }

    /**
     * Processes sub-elements of Geometrie element.
     *
     * @param reader    XML stream reader
     * @param item      item
     * @param namespace namespace of Geometrie elements
     * @param logFile   log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processGeometrieElement(final XMLStreamReader reader,
            final Object item, final String namespace, final Writer logFile)
            throws XMLStreamException {
        if (namespace.equals(reader.getNamespaceURI())) {
            final String localName = reader.getLocalName();

            if ("DefinicniBod".equals(localName)
                    && item instanceof ItemWithDefinicniBod) {
                final ItemWithDefinicniBod itemDefinicniBod =
                        (ItemWithDefinicniBod) item;
                itemDefinicniBod.setDefinicniBod(processDefinicniBod(
                        reader, namespace, localName, logFile));
            } else {
                printWarningIgnoringElement(logFile, reader);
            }
        } else {
            printWarningIgnoringElement(logFile, reader);
        }
    }

    /**
     * Processes MluvnickeCharakteristiky element.
     *
     * @param reader       XML stream reader
     * @param item         item
     * @param endNamespace namespace of MluvnickeCharakteristiky
     * @param logFile      log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processMluvnickeCharakteristiky(
            final XMLStreamReader reader, final ItemWithMluvCharPad item,
            final String endNamespace, final Writer logFile)
            throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processMluvnickeCharakteristikyElement(
                            reader, item, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(
                            endNamespace, "MluvnickeCharakteristiky", reader)) {
                        return;
                    }
            }
        }
    }

    /**
     * Processes sub-elements of MluvnickeCharakteristiky element.
     *
     * @param reader  XML stream reader
     * @param item    item
     * @param logFile log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processMluvnickeCharakteristikyElement(
            final XMLStreamReader reader, final ItemWithMluvCharPad item,
            final Writer logFile) throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case Namespaces.COMMON_TYPY:
                switch (reader.getLocalName()) {
                    case "Pad2":
                        item.setMluvCharPad2(reader.getElementText());
                        break;
                    case "Pad3":
                        item.setMluvCharPad3(reader.getElementText());
                        break;
                    case "Pad4":
                        item.setMluvCharPad4(reader.getElementText());
                        break;
                    case "Pad5":
                        item.setMluvCharPad5(reader.getElementText());
                        break;
                    case "Pad6":
                        item.setMluvCharPad6(reader.getElementText());
                        break;
                    case "Pad7":
                        item.setMluvCharPad7(reader.getElementText());
                        break;
                    default:
                        Utils.printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                printWarningIgnoringElement(logFile, reader);
        }
    }
}
