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

import java.util.List;

/**
 * WKT utilities.
 *
 * @author fordfrog
 */
public class WKTUtils {

    /**
     * Appends SRID information to the string builder if SRID is not null.
     *
     * @param sbString string builder
     * @param srid     SRID or null
     */
    public static void appendSrid(final StringBuilder sbString,
            final Integer srid) {
        if (srid == null) {
            return;
        }

        sbString.append("SRID=");
        sbString.append(srid);
        sbString.append(';');
    }

    /**
     * Appends points to the string builder.
     *
     * @param sbString string builder
     * @param points   list of points
     */
    public static void appendPoints(final StringBuilder sbString,
            final List<Point> points) {
        boolean first = true;

        for (final Point point : points) {
            if (first) {
                first = false;
            } else {
                sbString.append(',');
            }

            sbString.append(point.getX());
            sbString.append(' ');
            sbString.append(point.getY());
        }
    }

    /**
     * Creates new instance of WKTUtils.
     */
    private WKTUtils() {
    }
}
