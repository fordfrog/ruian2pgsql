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

import com.fordfrog.ruian2pgsql.Config;
import com.fordfrog.ruian2pgsql.containers.ItemWithDefinicniBod;
import com.fordfrog.ruian2pgsql.containers.ItemWithDefinicniCara;
import com.fordfrog.ruian2pgsql.containers.ItemWithEmergency;
import com.fordfrog.ruian2pgsql.containers.ItemWithHranice;
import com.fordfrog.ruian2pgsql.containers.ItemWithMluvCharPad;
import com.fordfrog.ruian2pgsql.gml.GMLParser;
import com.fordfrog.ruian2pgsql.gml.GMLReader;
import java.sql.Connection;
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
     * Processes DefinicniBod element.
     *
     * @param reader       XML stream reader
     * @param con          database connection
     * @param item         item
     * @param endNamespace namespace of DefinicniBod element
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processDefinicniBod(final XMLStreamReader reader,
            final Connection con, final Object item, final String endNamespace)
            throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processDefinicniBodElement(
                            reader, con, item, endNamespace);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            endNamespace, "DefinicniBod", reader)) {
                        return;
                    }
            }
        }
    }

    /**
     * Processes sub-elements of DefinicniBod element.
     *
     * @param reader    XML stream reader
     * @param con       database connection
     * @param item      item
     * @param namespace namespace
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processDefinicniBodElement(final XMLStreamReader reader,
            final Connection con, final Object item, final String namespace)
            throws XMLStreamException {
        final String curNamespace = reader.getNamespaceURI();

        if (namespace.equals(curNamespace)) {
            final String localName = reader.getLocalName();

            if ("AdresniBod".equals(localName)
                    && item instanceof ItemWithDefinicniBod) {
                final ItemWithDefinicniBod itemDefinicniBod =
                        (ItemWithDefinicniBod) item;
                itemDefinicniBod.setDefinicniBod(
                        processGML(reader, con, namespace, "AdresniBod"));
            } else if ("Hasici".equals(localName)
                    && item instanceof ItemWithEmergency) {
                final ItemWithEmergency itemEmergency =
                        (ItemWithEmergency) item;
                itemEmergency.setHasici(
                        processGML(reader, con, namespace, "Hasici"));
            } else if ("Zachranka".equals(localName)
                    && item instanceof ItemWithEmergency) {
                final ItemWithEmergency itemEmergency =
                        (ItemWithEmergency) item;
                itemEmergency.setZachranka(
                        processGML(reader, con, namespace, "Zachranka"));
            } else {
                XMLUtils.processUnsupported(reader);
            }
        } else if (Namespaces.GML.equals(curNamespace)
                && item instanceof ItemWithDefinicniBod) {
            final ItemWithDefinicniBod itemDefinicniBod =
                    (ItemWithDefinicniBod) item;
            itemDefinicniBod.setDefinicniBod(processGML(
                    reader, con, curNamespace, reader.getLocalName()));
        } else {
            XMLUtils.processUnsupported(reader);
        }
    }

    /**
     * Reads Kod of AdresniMisto from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getAdresniMistoKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.BASE_TYPY, "Kod",
                endNamespace, "AdresniMistoKod").intValue();
    }

    /**
     * Reads Kod of CastObce from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getCastObceKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.CAST_OBCE_INT_TYPY, "Kod",
                endNamespace, "CastObce").intValue();
    }

    /**
     * Reads Kod of IdentifikacniParcela from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Long getIdentifikacniParcelaId(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.PARCELA_INT_TYPY, "Id",
                endNamespace, "IdentifikacniParcela");
    }

    /**
     * Reads Kod of KatastralniUzemi from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getKatastralniUzemiKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.KAT_UZ_INT_TYPY, "Kod",
                endNamespace, "KatastralniUzemi").intValue();
    }

    /**
     * Reads Kod of Kraj from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getKrajKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.KRAJ_INT_TYPY, "Kod",
                endNamespace, "Kraj").intValue();
    }

    /**
     * Reads Kod of Momc from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getMomcKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.MOMC_INT_TYPY, "Kod",
                endNamespace, "Momc").intValue();
    }

    /**
     * Reads Kod of Mop from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getMopKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.MOP_INT_TYPY, "Kod",
                endNamespace, "Mop").intValue();
    }

    /**
     * Reads Kod of Obec from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getObecKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.OBEC_INT_TYPY, "Kod",
                endNamespace, "Obec").intValue();
    }

    /**
     * Reads Kod of Okres from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getOkresKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.OKRES_INT_TYPY, "Kod",
                endNamespace, "Okres").intValue();
    }

    /**
     * Reads Kod of Orp from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getOrpKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.ORP_INT_TYPY, "Kod",
                endNamespace, "Orp").intValue();
    }

    /**
     * Reads Kod of Pou from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getPouKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.POU_INT_TYPY, "Kod",
                endNamespace, "Pou").intValue();
    }

    /**
     * Reads Kod of RegionSoudrznosti from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getRegionSoudrznostiKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.REG_SOU_INTI_TYPY, "Kod",
                endNamespace, "RegionSoudrznosti").intValue();
    }

    /**
     * Reads Kod of SpravniObvod from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getSpravniObvodKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.SPRAV_OBV_INT_TYPY, "Kod",
                endNamespace, "SpravniObvod").intValue();
    }

    /**
     * Reads Kod of Stat from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getStatKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.STAT_INT_TYPY, "Kod",
                endNamespace, "Stat").intValue();
    }

    /**
     * Reads Kod of StavebniObjekt from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getStavebniObjektKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.STAV_OBJ_INT_TYPY, "Kod",
                endNamespace, "StavebniObjekt").intValue();
    }

    /**
     * Reads Kod of Ulice from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getUliceKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.ULICE_INT_TYPY, "Kod",
                endNamespace, "Ulice").intValue();
    }

    /**
     * Reads Kod of Vusc from current element.
     *
     * @param reader       XML stream reader
     * @param endNamespace end namespace of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static Integer getVuscKod(final XMLStreamReader reader,
            final String endNamespace) throws XMLStreamException {
        return getElementValue(reader, Namespaces.VUSC_INT_TYPY, "Kod",
                endNamespace, "Vusc").intValue();
    }

    /**
     * Reads value from specified element.
     *
     * @param reader         XML stream reader
     * @param valueNamespace namespace of the value element
     * @param valueLocalName local name of the value element
     * @param endNamespace   end namespace of current element
     * @param endElement     local name of current element
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Long getElementValue(final XMLStreamReader reader,
            final String valueNamespace, final String valueLocalName,
            final String endNamespace, final String endElement)
            throws XMLStreamException {
        Long kod = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    kod = getElementValueFromElement(
                            reader, valueNamespace, valueLocalName);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            endNamespace, endElement, reader)) {
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
     *
     * @return Kod value
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static Long getElementValueFromElement(final XMLStreamReader reader,
            final String namespace, final String localName)
            throws XMLStreamException {
        if (XMLUtils.isSameElement(namespace, localName, reader)) {
            return Long.valueOf(reader.getElementText());
        } else {
            XMLUtils.processUnsupported(reader);
        }

        return null;
    }

    /**
     * Processes Geometrie element. If item supports
     * {@link ItemWithDefinicniBod} interface and Geometrie element contains
     * DefinicniBod element, then DefinicniBod value is set to the item.
     *
     * @param reader    XML stream reader
     * @param con       database connection
     * @param item      item
     * @param namespace namespace of Geometrie element
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processGeometrie(final XMLStreamReader reader,
            final Connection con, final Object item, final String namespace)
            throws XMLStreamException {
        if (Config.isNoGis()) {
            XMLUtils.skipCurrentElement(reader);
        } else {
            while (reader.hasNext()) {
                final int event = reader.next();

                switch (event) {
                    case XMLStreamReader.START_ELEMENT:
                        processGeometrieElement(reader, con, item, namespace);
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        if (XMLUtils.isSameElement(
                                namespace, "Geometrie", reader)) {
                            return;
                        }
                }
            }
        }
    }

    /**
     * Processes sub-elements of Geometrie element.
     *
     * @param reader    XML stream reader
     * @param con       database connection
     * @param item      item
     * @param namespace namespace of Geometrie elements
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processGeometrieElement(final XMLStreamReader reader,
            final Connection con, final Object item, final String namespace)
            throws XMLStreamException {
        if (namespace.equals(reader.getNamespaceURI())) {
            final String localName = reader.getLocalName();

            if ("DefinicniBod".equals(localName)) {
                processDefinicniBod(reader, con, item, namespace);
            } else if ("DefinicniCara".equals(localName)
                    && item instanceof ItemWithDefinicniCara) {
                final ItemWithDefinicniCara itemDefinicniCara =
                        (ItemWithDefinicniCara) item;
                itemDefinicniCara.setDefinicniCara(
                        processGML(reader, con, namespace, localName));
            } else if ("OriginalniHranice".equals(localName)
                    && item instanceof ItemWithHranice) {
                final ItemWithHranice itemHranice = (ItemWithHranice) item;
                itemHranice.setHranice(
                        processGML(reader, con, namespace, localName));
            } else if ("GeneralizovaneHranice3".equals(localName)
                    && item instanceof ItemWithHranice) {
                final ItemWithHranice itemHranice = (ItemWithHranice) item;
                itemHranice.setHranice(
                        processGML(reader, con, namespace, localName));
            } else {
                XMLUtils.processUnsupported(reader);
            }
        } else {
            XMLUtils.processUnsupported(reader);
        }
    }

    /**
     * Processes MluvnickeCharakteristiky element.
     *
     * @param reader       XML stream reader
     * @param item         item
     * @param endNamespace namespace of MluvnickeCharakteristiky
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processMluvnickeCharakteristiky(
            final XMLStreamReader reader, final ItemWithMluvCharPad item,
            final String endNamespace) throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processMluvnickeCharakteristikyElement(reader, item);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            endNamespace, "MluvnickeCharakteristiky", reader)) {
                        return;
                    }
            }
        }
    }

    /**
     * Processes sub-elements of MluvnickeCharakteristiky element.
     *
     * @param reader XML stream reader
     * @param item   item
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processMluvnickeCharakteristikyElement(
            final XMLStreamReader reader, final ItemWithMluvCharPad item)
            throws XMLStreamException {
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
                        XMLUtils.processUnsupported(reader);
                }

                break;
            default:
                XMLUtils.processUnsupported(reader);
        }
    }

    /**
     * Processes GML XML. Depending on the output format (GML or EWKT)
     * appropriate method is called.
     *
     * @param reader       XML stream reader
     * @param con          database connection
     * @param endNamespace end namespace
     * @param endLocalName end local name
     *
     * @return parsed GML as string (either GML or EWKT)
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static String processGML(final XMLStreamReader reader,
            final Connection con, final String endNamespace,
            final String endLocalName) throws XMLStreamException {
        final String result;

        if (Config.isConvertToEWKT()) {
            if (Config.isLinearizeEWKT()) {
                result =
                    GMLParser.parseLinearized(reader, endNamespace, endLocalName, Config.getLinearPrecision()).toWKT();
            } else {
                result =
                    GMLParser.parse(reader, endNamespace, endLocalName).toWKT();
            }
        } else {
            result = GMLReader.readGML(reader, con, endNamespace, endLocalName);
        }

        if (Config.isDebug()) {
            Log.write(result);
        }

        return result;
    }
}
