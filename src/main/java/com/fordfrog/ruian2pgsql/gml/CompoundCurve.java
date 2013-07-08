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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * CompoundCurve.
 *
 * @author fordfrog
 */
public class CompoundCurve extends AbstractGeometry implements CurvedGeometry<Line> {

    /**
     * Segments of the curve.
     */
    private final List<Geometry> segments = new ArrayList<>(5);

    /**
     * Adds segment to the list of segments.
     *
     * @param segment segment
     */
    public void addSegment(final Geometry segment) {
        segments.add(segment);
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString =
                new StringBuilder(segments.size() * 1_024);
        WKTUtils.appendSrid(sbString, getSrid());

        sbString.append("COMPOUNDCURVE(");

        boolean first = true;

        for (final Geometry segment : segments) {
            if (first) {
                first = false;
            } else {
                sbString.append(',');
            }

            sbString.append(segment.toWKT());
        }

        sbString.append(')');

        return sbString.toString();
    }

    @Override
    public Line linearize(final double precision) {
        final Line line = new Line();
        line.setSrid(getSrid());
        List<Point> points = null;
        Point lastPoint = null;

        for (final Geometry segment : segments) {
            if (segment instanceof Line) {
                points = ((Line) segment).getPoints();
            } else {
                points = ((CurvedGeometry<Line>) segment).linearize(precision).getPoints();
            }

            if (lastPoint != null) {
                if (lastPoint.getX() != points.get(0).getX() ||
                    lastPoint.getY() != points.get(0).getY()) {
                    throw new RuntimeException(MessageFormat.format(
                        "Could not connect segments of CompoundCurve: {0} != {1}.",
                        lastPoint.toWKT(), points.get(0).toWKT()));
                }
            } else {
                line.addPoint(points.get(0));
            }

            for (final Point point : points.subList(1, points.size())) {
                line.addPoint(point);
            }
            lastPoint = points.get(points.size() - 1);
        }

        return line;
    }
}
