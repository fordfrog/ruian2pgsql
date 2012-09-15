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
import java.util.Collections;
import java.util.List;

/**
 * Polygon.
 *
 * @author fordfrog
 */
public class Polygon implements Geometry {

    /**
     * Inner geometries.
     */
    private final List<Geometry> inners = new ArrayList<>(5);
    /**
     * SRID.
     */
    private Integer srid;
    /**
     * Outer geometry.
     */
    private Geometry outer;

    /**
     * Getter for {@link #outer}.
     *
     * @return {@link #outer}
     */
    public Geometry getOuter() {
        return outer;
    }

    /**
     * Setter for {@link #outer}.
     *
     * @param outer {@link #outer}
     */
    public void setOuter(final Geometry outer) {
        this.outer = outer;
    }

    /**
     * Adds inner geometry to the list of inner geometries.
     *
     * @param inner inner geometry
     */
    public void addInner(final Geometry inner) {
        inners.add(inner);
    }

    /**
     * Getter for {@link #inners}.
     *
     * @return {@link #inners}
     */
    public List<Geometry> getInners() {
        return Collections.unmodifiableList(inners);
    }

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
    public String toWKT() {
        final StringBuilder sbString =
                new StringBuilder((inners.size() + 1) * 1_024);
        WKTUtils.appendSrid(sbString, srid);

        if (isCurvePolygon()) {
            sbString.append("CURVEPOLYGON(");
        } else {
            sbString.append("POLYGON(");
        }

        sbString.append(outer.toWKT());

        for (final Geometry inner : inners) {
            sbString.append(',');
            sbString.append(inner.toWKT());
        }

        sbString.append(')');

        return sbString.toString().replace("LINESTRING", "");
    }

    /**
     * Checks whether the polygon is curve polygon.
     *
     * @return true if it is curve polygon, otherwise false
     */
    private boolean isCurvePolygon() {
        if (outer instanceof CompoundCurve) {
            return true;
        }

        for (final Geometry inner : inners) {
            if (inner instanceof CompoundCurve) {
                return true;
            }
        }

        return false;
    }
}
