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

/**
 * Point.
 *
 * @author fordfrog
 */
public class Point implements Geometry {

    /**
     * SRID.
     */
    private Integer srid;
    /**
     * X coordinate.
     */
    private double x;
    /**
     * Y coordinate.
     */
    private double y;

    /**
     * Creates new instance of Point.
     */
    public Point() {
    }

    /**
     * Creates new instance of Point.
     *
     * @param x {@link #x}
     * @param y {@link #y}
     */
    public Point(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for {@link #x}.
     *
     * @return {@link #x}
     */
    public double getX() {
        return x;
    }

    /**
     * Setter for {@link #x}.
     *
     * @param x {@link #x}
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * Getter for {@link #y}.
     *
     * @return {@link #y}
     */
    public double getY() {
        return y;
    }

    /**
     * Setter for {@link #y}.
     *
     * @param y {@link #y}
     */
    public void setY(final double y) {
        this.y = y;
    }

    @Override
    public Integer getSrid() {
        return srid;
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString = new StringBuilder(50);
        WKTUtils.appendSrid(sbString, srid);

        sbString.append("POINT(");
        sbString.append(x);
        sbString.append(' ');
        sbString.append(y);
        sbString.append(')');

        return sbString.toString();
    }

    /**
     * Setter for {@link #srid}.
     *
     * @param srid {@link #srid}
     */
    public void setSrid(final Integer srid) {
        this.srid = srid;
    }
}
