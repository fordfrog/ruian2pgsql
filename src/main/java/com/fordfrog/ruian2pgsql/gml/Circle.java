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
public class Circle extends AbstractGeometry implements GeometryWithPoints, CurvedGeometry<Line> {

    /**
     * Circle points.
     */
    private final List<Point> points = new ArrayList<>(5);

    @Override
    public void addPoint(final Point point) {
        points.add(point);
    }

    @Override
    public String toWKT() {
        if (points.size() != 3) {
            throw new RuntimeException(MessageFormat.format(
                "Invalid Circle definition: need 3 control points, but got {0}.",
                points.size()));
        }

        final Point center = GeometryUtils.getArcCenter(points.get(0), points.get(1), points.get(2));
        final double dx = center.getX() - points.get(0).getX();
        final double dy = center.getY() - points.get(0).getY();

        final Curve curve = new Curve();
        curve.addPoint(points.get(0));
        curve.addPoint(new Point(center.getX() - dy, center.getY() + dx));
        curve.addPoint(new Point(center.getX() + dx, center.getY() + dy));
        curve.addPoint(new Point(center.getX() + dy, center.getY() - dx));
        curve.addPoint(points.get(0));

        return curve.toWKT();
    }

    @Override
    public Line linearize(final double precision) {
        if (points.size() != 3) {
            throw new RuntimeException(MessageFormat.format(
                "Invalid Circle definition: need 3 control points, but got {0}.",
                points.size()));
        }

        final Point center = GeometryUtils.getArcCenter(points.get(0), points.get(1), points.get(2));
        final double radius = GeometryUtils.distance(points.get(0), center);
        final double a1 = Math.atan2(points.get(0).getY() - center.getY(),
                                     points.get(0).getX() - center.getX());

        double segmentCount = 3.0;
        if (0.5 * radius > precision) {
            segmentCount = Math.ceil(Math.PI / Math.acos(1 - precision / radius));
        }

        final Line line = new Line();
        line.setSrid(getSrid());
        line.addPoint(points.get(0));
        for (int i = 1; i < segmentCount; i++) {
            double a = a1 + i * 2 * Math.PI / segmentCount;
            line.addPoint(new Point(center.getX() + radius * Math.cos(a),
                                 center.getY() + radius * Math.sin(a)));
        }
        line.addPoint(points.get(0));

        return line;
    }
}
