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
        final double dx1 = point2.getX() - point1.getX();
        final double dy1 = point2.getY() - point1.getY();
        final double dx2 = point3.getX() - point1.getX();
        final double dy2 = point3.getY() - point1.getY();

        final double ac = dx1 * dy2;
        final double bd = dx2 * dy1;
        if (ac == bd) {
            throw new RuntimeException(
                        "Invalid Circle definition: points are co-linear.");
        }
        final double idet = 0.5 / (ac - bd);

        final double dxs = (dy1 * dy2 * (point2.getY() - point3.getY())
                            + dx1 * ac - dx2 * bd) * idet;
        final double dys = (dx1 * dx2 * (point3.getX() - point2.getX())
                            + dy2 * ac - dy1 * bd) * idet;
        return new Point(point1.getX() + dxs, point1.getY() + dys);
    }

    /**
     * Creates new instance of GeometryUtils.
     */
    private GeometryUtils() {
    }
}
