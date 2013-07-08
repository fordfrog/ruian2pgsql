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
import java.util.Collections;
import java.util.List;

/**
 * Curve.
 *
 * @author fordfrog
 */
public class Curve extends AbstractGeometry implements GeometryWithPoints, CurvedGeometry<Line> {

    /**
     * Curve points.
     */
    private final List<Point> points = new ArrayList<>(5);

    @Override
    public void addPoint(final Point point) {
        points.add(point);
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString = new StringBuilder(100);
        WKTUtils.appendSrid(sbString, getSrid());

        sbString.append("CIRCULARSTRING(");
        WKTUtils.appendPoints(sbString, points);
        sbString.append(')');

        return sbString.toString();
    }

    @Override
    public Line linearize(final double precision) {
        if (points.size() < 3) {
            throw new RuntimeException(MessageFormat.format(
                "Invalid Curve definition: need at least 3 control points, but got {0}.",
                points.size()));
        }

        final Line line = new Line();
        line.setSrid(getSrid());
        line.addPoint(points.get(0));
        for (int i = 2; i < points.size(); i = i + 2) {
            List<Point> linePoints = linearizeArc(precision,
                        points.get(i - 2), points.get(i - 1), points.get(i));
            for (final Point point : linePoints.subList(1, linePoints.size())) {
                line.addPoint(point);
            }
        }

        return line;
    }

    /**
     * Calculates linear approximation of the arc.
     *
     * @param precision of linear approximation
     * @param point1 first point of the arc
     * @param point2 second point of the arc
     * @param point3 third point of the arc
     *
     * @return list of points approximating arc
     */
    private List<Point> linearizeArc(double precision, final Point point1,
            final Point point2, final Point point3) {
        final Point center = GeometryUtils.getArcCenter(point1, point2, point3);
        final double radius = GeometryUtils.distance(point2, center);
        final boolean ccw = GeometryUtils.orientationDet(point1, point2, point3) > 0;
        final double a1 = Math.atan2(point1.getY() - center.getY(),
                                     point1.getX() - center.getX());
        final double a3 = Math.atan2(point3.getY() - center.getY(),
                                     point3.getX() - center.getX());
        double da = a3 - a1;
        da = ccw ? da : -da;
        da = da > 0 ? da : da + 2 * Math.PI;

        double segmentCount = 1.0;
        if (2 * radius > precision) {
            segmentCount = Math.ceil(0.5 * da / Math.acos(1 - precision / radius));
        }

        final List<Point> points = new ArrayList<>(100);
        for (int i = 1; i < segmentCount; i++) {
            double a = (ccw ? a1 : a3) + i * da / segmentCount;
            points.add(new Point(center.getX() + radius * Math.cos(a),
                                 center.getY() + radius * Math.sin(a)));
        }
        if (!ccw) {
            Collections.reverse(points);
        }

        points.add(0, point1);
        points.add(point3);

        return points;
    }

}
