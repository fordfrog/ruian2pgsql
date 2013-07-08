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

/**
 * Geometry utilities.
 *
 * @author xificurk
 */
public class GeometryUtils {

    /**
     * Calculates distance of two points.
     *
     * @param point1 first point
     * @param point2 second point
     *
     * @return distance of points
     */
    public static double distance(final Point point1, final Point point2) {
        return Math.hypot(point2.getX() - point1.getX(),
                          point2.getY() - point1.getY());
    }

    /**
     * Calculates determinant of orientation matrix.
     *
     * @param point1 first point
     * @param point2 second point
     * @param point3 third point
     *
     * @return determinant of orientation matrix
     */
    public static double orientationDet(final Point point1, final Point point2, final Point point3) {
        return (point3.getX() - point2.getX()) * (point1.getY() - point2.getY()) -
               (point1.getX() - point2.getX()) * (point3.getY() - point2.getY());
    }

    /**
     * Calculates center of the arc.
     *
     * @param point1 first point of the arc
     * @param point2 second point of the arc
     * @param point3 third point of the arc
     *
     * @return center of the arc.
     */
    public static Point getArcCenter(final Point point1, final Point point2,
            final Point point3) {
        final double dx1 = point1.getX() - point2.getX();
        final double dy1 = point1.getY() - point2.getY();
        final double dx3 = point3.getX() - point2.getX();
        final double dy3 = point3.getY() - point2.getY();

        final double ac = dx3 * dy1;
        final double bd = dx1 * dy3;
        if (ac == bd) {
            throw new RuntimeException(
                        "Invalid Circle definition: points are co-linear.");
        }
        final double idet = 0.5 / (ac - bd);

        final double dxs = (dy3 * dy1 * (point3.getY() - point1.getY())
                            + (dx3 * ac - dx1 * bd)) * idet;
        final double dys = (dx3 * dx1 * (point1.getX() - point3.getX())
                            + (dy1 * ac - dy3 * bd)) * idet;
        return new Point(point2.getX() + dxs, point2.getY() + dys);
    }

    /**
     * Creates new instance of GeometryUtils.
     */
    private GeometryUtils() {
    }
}
