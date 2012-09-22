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
public class Circle extends AbstractGeometry implements GeometryWithPoints {

    /**
     * Circle points.
     */
    private final List<Point> points = new ArrayList<>(5);

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

        final double dx1 = points.get(1).getX() - points.get(0).getX();
        final double dy1 = points.get(1).getY() - points.get(0).getY();
        final double dx2 = points.get(2).getX() - points.get(0).getX();
        final double dy2 = points.get(2).getY() - points.get(0).getY();

        final double ac = dx1 * dy2;
        final double bd = dx2 * dy1;
        if (ac == bd) {
            throw new RuntimeException(
                        "Invalid Circle definition: points are co-linear.");
        }
        final double idet = 0.5 / (ac - bd);

        final double dxs = (dy1 * dy2 * (points.get(1).getY() - points.get(2).getY())
                            + dx1 * ac - dx2 * bd) * idet;
        final double dys = (dx1 * dx2 * (points.get(2).getX() - points.get(1).getX())
                            + dy2 * ac - dy1 * bd) * idet;
        final double xs = points.get(0).getX() + dxs;
        final double ys = points.get(0).getY() + dys;

        final List<Point> circularStringPoints = new ArrayList<>(5);
        circularStringPoints.add(points.get(0));
        circularStringPoints.add(new Point(xs - dys, ys + dxs));
        circularStringPoints.add(new Point(xs + dxs, ys + dys));
        circularStringPoints.add(new Point(xs + dys, ys - dxs));
        circularStringPoints.add(points.get(0));

        return circularStringPoints;
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString = new StringBuilder(100);
        WKTUtils.appendSrid(sbString, getSrid());

        sbString.append("CIRCULARSTRING(");
        WKTUtils.appendPoints(sbString, getCircularStringPoints());
        sbString.append(")");

        return sbString.toString();
    }
}
