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

import java.util.ArrayList;
import java.util.List;

/**
 * MultiPolygon.
 *
 * @author fordfrog
 */
public class MultiPolygon extends AbstractGeometry implements CurvedGeometry<MultiPolygon> {

    /**
     * List of polygons.
     */
    private final List<Polygon> polygons = new ArrayList<>(2);

    @Override
    public String toWKT() {
        final StringBuilder sbString = new StringBuilder(1000);
        WKTUtils.appendSrid(sbString, getSrid());

        if (hasArc()) {
            sbString.append("MULTISURFACE(");
        } else {
            sbString.append("MULTIPOLYGON(");
        }

        boolean first = true;

        for (final Polygon polygon : polygons) {
            if (first) {
                first = false;
            } else {
                sbString.append(',');
            }

            sbString.append(polygon.toWKT().replaceFirst("^POLYGON", ""));
        }

        sbString.append(')');

        return sbString.toString();
    }

    /**
     * Adds polygon to the list of polygons.
     *
     * @param polygon polygon
     */
    public void addPolygon(final Polygon polygon) {
        polygons.add(polygon);
    }

    /**
     * Checks whether the multipolygon has arc.
     *
     * @return true if the multipolygon has arc, otherwise false
     */
    public boolean hasArc() {
        for (final Polygon polygon : polygons) {
            if (polygon.hasArc()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MultiPolygon linearize(final double precision) {
        final MultiPolygon multiPolygon = new MultiPolygon();
        multiPolygon.setSrid(getSrid());

        for (final Polygon polygon : polygons) {
            multiPolygon.addPolygon(polygon.linearize(precision));
        }

        return multiPolygon;
    }
}
