/**
 * Copyright 2012 Petr Morávek
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Circle.
 *
 * @author xificurk
 */
public class Circle implements Geometry, GeometryWithPoints {

    /**
     * Circle points.
     */
    private final List<Point> points = new ArrayList<>(5);
    /**
     * SRID.
     */
    private Integer srid;

    @Override
    public Integer getSrid() {
        return srid;
    }

    /**
     * Setter for {@link #srid}.
     *
     * @param srid {@link #srid}
     */
    public void setSrid(final Integer srid) {
        this.srid = srid;
    }

    @Override
    public void addPoint(final Point point) {
        points.add(point);
    }

    /**
     * Calculate points for WKT Circular String.
     */
    private List<Point> getCircularStringPoints() {
        if (points.size() != 3) {
            throw new RuntimeException(MessageFormat.format(
                "Invalid Circle definition: need 3 control points, but got {0}.",
                points.size()));
        }

        final double ac = (points.get(1).getX() - points.get(0).getX()) *
                          (points.get(2).getY() - points.get(0).getY());
        final double bd = (points.get(2).getX() - points.get(0).getX()) *
                          (points.get(1).getY() - points.get(0).getY());
        if (ac == bd) {
            throw new RuntimeException(
                        "Invalid Circle definition: points are co-linear.");
        }
        final double idet = 1.0 / (ac - bd);

        final double dx = (points.get(1).getX() - points.get(0).getX()) *
                          (points.get(2).getX() - points.get(0).getX()) *
                          (points.get(2).getX() - points.get(1).getX());
        final double dy = (points.get(1).getY() - points.get(0).getY()) *
                          (points.get(2).getY() - points.get(0).getY()) *
                          (points.get(1).getY() - points.get(2).getY());

        final double x = idet * (dy + points.get(1).getX() * ac - points.get(2).getX() * bd);
        final double y = idet * (dx + points.get(2).getY() * ac - points.get(1).getY() * bd);

        final List<Point> circularStringPoints = new ArrayList<>(3);
        circularStringPoints.add(points.get(0));
        circularStringPoints.add(new Point(x, y));
        circularStringPoints.add(points.get(0));

        return circularStringPoints;
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString = new StringBuilder(100);
        WKTUtils.appendSrid(sbString, srid);

        sbString.append("CIRCULARSTRING(");
        WKTUtils.appendPoints(sbString, getCircularStringPoints());
        sbString.append(")");

        return sbString.toString();
    }
}
