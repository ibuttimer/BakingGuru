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

import static ie.ianbuttimer.bakingguru.data.AbstractResultWrapper.INVALID_ERROR_CODE;

/**
 * Class representing details of an error
 */
@SuppressWarnings("unused")
@Parcel
public class ErrorTuple extends Tuple<Integer, String, String> {

    /**
     * Default constructor
     */
    public ErrorTuple() {
        super(INVALID_ERROR_CODE, "", "");
    }

    /**
     * Constructor
     * @param errorCode     Error code
     * @param errorString   Error string
     * @param errorDetail   Error detail string
     */
    public ErrorTuple(Integer errorCode, String errorString, String errorDetail) {
        super(errorCode, errorString, errorDetail);
    }


    public int getErrorCode() {
        return getT1();
    }

    public void setErrorCode(int errorCode) {
        setT1(errorCode);
    }

    public String getErrorString() {
        return getT2();
    }

    public void setErrorString(String errorString) {
        setT2(errorString);
    }

    public String getErrorDetail() {
        return getT3();
    }

    public void setErrorDetail(String errorDetail) {
        setT3(errorDetail);
    }
}
