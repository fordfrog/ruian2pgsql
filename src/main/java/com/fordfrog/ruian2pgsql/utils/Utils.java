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
import com.fordfrog.ruian2pgsql.containers.ItemWithHranice;
import com.fordfrog.ruian2pgsql.containers.ItemWithMluvCharPad;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
     * Processes unsupported element and its subelements.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processUnsupported(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        processUnsupported(reader, 0, logFile);
    }

    /**
     * Processes unsupported element and its subelements.
     *
     * @param reader  XML stream reader
     * @param indent  indentation count
     * @param logFile log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processUnsupported(final XMLStreamReader reader,
            final int indent, final Writer logFile) throws XMLStreamException {
        final String namespace = reader.getNamespaceURI();
        final String localName = reader.getLocalName();
        final StringBuilder sbString = new StringBuilder(namespace.length()
                + localName.length() + 50);

        for (int i = 0; i < indent; i++) {
            sbString.append("  ");
        }

        sbString.append("Warning: Ignoring unsupported element ");
        sbString.append(namespace);
        sbString.append(' ');
        sbString.append(localName);

        printToLog(logFile, sbString.toString());

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processUnsupported(reader, indent + 1, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(namespace, localName, reader)) {
                        return;
                    }
            }
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
     * Flushes log buffer.
     *
     * @param logFile log file writer
     */
    public static void flushLog(final Writer logFile) {
        try {
            logFile.flush();
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to flush log", ex);
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
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static String processDefinicniBod(final XMLStreamReader reader,
            final String endNamespace, final String engLocalName,
            final Writer logFile) throws XMLStreamException {
        String definicniBod = null;

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
     * Processes OriginalniHranice element.
     *
     * @param reader       XML stream reader
     * @param endNamespace namespace of DefinicniBod element
     * @param engLocalName local name of DefinicniBod element
     * @param logFile      log file writer
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static String processOriginalniHranice(final XMLStreamReader reader,
            final String endNamespace, final String engLocalName,
            final Writer logFile) throws XMLStreamException {
        String hranice = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    hranice = processOriginalniHraniceElement(reader, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(endNamespace, engLocalName, reader)) {
                        return hranice;
                    }
            }
        }

        return hranice;
    }

    /**
     * Processes sub-elements of DefinicniBod element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static String processDefinicniBodElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        String result = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.ADR_MISTO_INT_TYPY:
                switch (reader.getLocalName()) {
                    case "AdresniBod":
                        result = processAdresniBod(reader, logFile);
                        break;
                    default:
                        processUnsupported(reader, logFile);
                }
                break;
            case Namespaces.GML:
                result = XMLStringUtil.createString(reader);
                break;
            default:
                processUnsupported(reader, logFile);
        }

        return result;
    }

    /**
     * Processes sub-elements of OriginalniHranice element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static String processOriginalniHraniceElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        String result = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                result = XMLStringUtil.createString(reader);
                break;
            default:
                processUnsupported(reader, logFile);
        }

        return result;
    }

    /**
     * Processes AdresniBod element.
     *
     * @param reader  XML stream reader
     * @param logFile log file writer
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static String processAdresniBod(final XMLStreamReader reader,
            final Writer logFile) throws XMLStreamException {
        String result = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    result = processAdresniBodElement(reader, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (isEndElement(Namespaces.ADR_MISTO_INT_TYPY,
                            "AdresniBod", reader)) {
                        return result;
                    }
            }
        }

        return result;
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
    private static String processAdresniBodElement(
            final XMLStreamReader reader, final Writer logFile)
            throws XMLStreamException {
        String result = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                result = XMLStringUtil.createString(reader);
                break;
            default:
                processUnsupported(reader, logFile);
        }

        return result;
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
            processUnsupported(reader, logFile);
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
            } else if ("OriginalniHranice".equals(localName)
                    && item instanceof ItemWithHranice) {
                final ItemWithHranice itemHranice = (ItemWithHranice) item;
                itemHranice.setHranice(processOriginalniHranice(
                        reader, namespace, localName, logFile));
            } else {
                processUnsupported(reader, logFile);
            }
        } else {
            processUnsupported(reader, logFile);
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
                        processUnsupported(reader, logFile);
                }

                break;
            default:
                processUnsupported(reader, logFile);
        }
    }
}
