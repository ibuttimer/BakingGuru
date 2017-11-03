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

package ie.ianbuttimer.bakingguru.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.net.URL;

import ie.ianbuttimer.bakingguru.utils.ErrorTuple;
import ie.ianbuttimer.bakingguru.utils.UriURLPair;


/**
 * Wrapper class for ICallback results
 */
@SuppressWarnings("unused")
public abstract class AbstractResultWrapper {

    public enum ResultType { STRING, STRING_ARRAY, INTEGER, CURSOR, URI, BUNDLE, ERROR };

    public static final int INVALID_ERROR_CODE = 0;

    private ICallback.ResponseHandler handler;  // type of handler required to process this object
    protected UriURLPair request;           // uri/url used to make request

    protected String stringResult;          // returned from url & uri call
    protected String[] stringArrayResult;   // returned from uri call
    protected int intResult;                // returned from update & delete
    protected Cursor cursorResult;          // returned from query
    protected Uri uriResult;                // returned from insert
    protected Bundle bundleResult;          // returned from uri call

    // members for error result
    protected int errorCode;                // error result code
    protected String errorString;           // error result string
    protected String errorDetailString;     // detailed error result string

    private ResultType resultType;

    /**
     * Default constructor
     */
    protected AbstractResultWrapper() {
        handler = null;
        request = null;

        stringResult = "";
        stringArrayResult = null;
        intResult = 0;
        cursorResult = null;
        uriResult = null;
        bundleResult = null;

        errorCode = INVALID_ERROR_CODE;
        errorString = "";
        errorDetailString = "";

        resultType = null;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param urlRequest    Original request URL
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest, String stringResult) {
        this();
        initUrl(handler, urlRequest);
        this.stringResult = stringResult;
        this.resultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler           Type of handler required to process this object
     * @param urlRequest        Original request URL
     * @param errorCode         Error code
     * @param errorString       Error string
     * @param errorDetailString Error detail string
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest, int errorCode, String errorString, String errorDetailString) {
        this();
        initUrl(handler, urlRequest);
        this.errorCode = errorCode;
        this.errorString = errorString;
        this.errorDetailString = errorDetailString;
        this.resultType = ResultType.ERROR;
    }

    /**
     * Constructor
     * @param handler           Type of handler required to process this object
     * @param uriRequest        Original request Uri
     * @param errorCode         Error code
     * @param errorString       Error string
     * @param errorDetailString Error detail string
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, int errorCode, String errorString, String errorDetailString) {
        this();
        initUri(handler, uriRequest);
        this.errorCode = errorCode;
        this.errorString = errorString;
        this.errorDetailString = errorDetailString;
        this.resultType = ResultType.ERROR;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String stringResult) {
        this();
        initUri(handler, uriRequest);
        this.stringResult = stringResult;
        this.resultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler               Type of handler required to process this object
     * @param uriRequest            Original request Uri
     * @param stringArrayResult     String array response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String[] stringArrayResult) {
        this();
        initUri(handler, uriRequest);
        this.stringArrayResult = stringArrayResult;
        this.resultType = ResultType.STRING_ARRAY;
    }

    /**
     * Initialise the handler & uri
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     */
    protected void initUri(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest) {
        this.handler = handler;
        this.request = new UriURLPair(uriRequest);
    }

    /**
     * Initialise the handler & URL
     * @param handler       Type of handler required to process this object
     * @param urlRequest    Original request URL
     */
    protected void initUrl(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest) {
        this.handler = handler;
        this.request = new UriURLPair(urlRequest);
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param intResult     Integer response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, int intResult) {
        this();
        initUri(handler, uriRequest);
        this.intResult = intResult;
        this.resultType = ResultType.INTEGER;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param cursorResult  Cursor response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Cursor cursorResult) {
        this();
        initUri(handler, uriRequest);
        this.cursorResult = cursorResult;
        this.resultType = ResultType.CURSOR;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param uriResult     Uri response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Uri uriResult) {
        this();
        initUri(handler, uriRequest);
        this.uriResult = uriResult;
        this.resultType = ResultType.URI;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param bundleResult  Bundle response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Bundle bundleResult) {
        this();
        initUri(handler, uriRequest);
        this.bundleResult = bundleResult;
        this.resultType = ResultType.BUNDLE;
    }

    public String getStringResult() {
        if (resultType != ResultType.STRING) {
            throw new IllegalStateException("Not string result");
        }
        return stringResult;
    }

    public String[] getStringArrayResult() {
        if (resultType != ResultType.STRING_ARRAY) {
            throw new IllegalStateException("Not string array result");
        }
        return stringArrayResult;
    }

    public int getIntResult() {
        if (resultType != ResultType.INTEGER) {
            throw new IllegalStateException("Not integer result");
        }
        return intResult;
    }

    public Cursor getCursorResult() {
        if (resultType != ResultType.CURSOR) {
            throw new IllegalStateException("Not cursor result");
        }
        return cursorResult;
    }

    public Uri getUriResult() {
        if (resultType != ResultType.URI) {
            throw new IllegalStateException("Not uri result");
        }
        return uriResult;
    }

    public Bundle getBundleResult() {
        if (resultType != ResultType.BUNDLE) {
            throw new IllegalStateException("Not bundle result");
        }
        return bundleResult;
    }

    public ErrorTuple getErrorResult() {
        if (resultType != ResultType.ERROR) {
            throw new IllegalStateException("Not error result");
        }
        return new ErrorTuple(errorCode, errorString, errorDetailString);
    }

    public ICallback.ResponseHandler getHandler() {
        return handler;
    }

    public Uri getUriRequest() {
        return request.getUri();
    }

    public URL getUrlRequest() {
        return request.getUrl();
    }

    public ResultType getResultType() {
        return resultType;
    }


    /**
     * Check if this object represents a result of the specified type
     * @param type  Type of result to check
     * @return  <code>true</code> if this object is a result of the specified type, <code>false</code> otherwise
     */
    public boolean isResultType(ResultType type) {
        return (resultType == type);
    }

    /**
     * Check if this object represents a string result
     * @return  <code>true</code> if this object is a string result, <code>false</code> otherwise
     */
    public boolean isString() {
        return isResultType(ResultType.STRING);
    }

    /**
     * Check if this object represents a string array result
     * @return  <code>true</code> if this object is a string array result, <code>false</code> otherwise
     */
    public boolean isStringArray() {
        return isResultType(ResultType.STRING_ARRAY);
    }

    /**
     * Check if this object represents an integer result
     * @return  <code>true</code> if this object is an integer result, <code>false</code> otherwise
     */
    public boolean isInteger() {
        return isResultType(ResultType.INTEGER);
    }

    /**
     * Check if this object represents a cursor result
     * @return  <code>true</code> if this object is a cursor result, <code>false</code> otherwise
     */
    public boolean isCursor() {
        return isResultType(ResultType.CURSOR);
    }

    /**
     * Check if this object represents a uri result
     * @return  <code>true</code> if this object is a uri result, <code>false</code> otherwise
     */
    public boolean isUri() {
        return isResultType(ResultType.URI);
    }

    /**
     * Check if this object represents a bundle result
     * @return  <code>true</code> if this object is a bundle result, <code>false</code> otherwise
     */
    public boolean isBundle() {
        return isResultType(ResultType.BUNDLE);
    }

    /**
     * Check if this object represents an error result
     * @return  <code>true</code> if this object is an error result, <code>false</code> otherwise
     */
    public boolean isError() {
        return isResultType(ResultType.ERROR);
    }


    public boolean isValidErrorCode() {
        return (isError() && (errorCode > INVALID_ERROR_CODE));
    }
}
