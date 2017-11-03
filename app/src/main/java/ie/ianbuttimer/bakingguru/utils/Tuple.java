/*
 * Copyright (c) 2017 Ian Buttimer.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.bakingguru.utils;

import org.parceler.Parcel;

/**
 * Three item tuple utility class
 */
@SuppressWarnings("unused")
public class Tuple<T1, T2, T3> {

    protected T1 t1;
    protected T2 t2;
    protected T3 t3;

    /**
     * Constructor
     * @param t1    First item
     * @param t2    Second item
     * @param t3    Third item
     */
    public Tuple(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public T1 getT1() {
        return t1;
    }

    public void setT1(T1 t1) {
        this.t1 = t1;
    }

    public T2 getT2() {
        return t2;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }

    public T3 getT3() {
        return t3;
    }

    public void setT3(T3 t3) {
        this.t3 = t3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?, ?> tuple = (Tuple<?, ?, ?>) o;

        if (t1 != null ? !t1.equals(tuple.t1) : tuple.t1 != null) return false;
        if (t2 != null ? !t2.equals(tuple.t2) : tuple.t2 != null) return false;
        return t3 != null ? t3.equals(tuple.t3) : tuple.t3 == null;

    }

    @Override
    public int hashCode() {
        int result = t1 != null ? t1.hashCode() : 0;
        result = 31 * result + (t2 != null ? t2.hashCode() : 0);
        result = 31 * result + (t3 != null ? t3.hashCode() : 0);
        return result;
    }
}
