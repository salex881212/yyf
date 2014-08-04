package com.alex.yyf.spider.utils;

import java.io.IOException;

import org.apache.http.annotation.Immutable;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

/**
 * TODO 自己实现retry，因为default的retry有BUG.
 *
 * @author work.
 *         Created 2013-4-13.
 */
@Immutable
public class BwopHttpRequestRetryHandler implements HttpRequestRetryHandler {
	
	/** the number of times a method will be retried */
    private final int retryCount;
    
    /**
     * Default constructor
     */
    @SuppressWarnings("javadoc")
	public BwopHttpRequestRetryHandler(int retryCount) {
        super();
        this.retryCount = retryCount;
    }

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
			HttpContext context) {
		// TODO Auto-generated method stub.
		if(this.retryCount > executionCount) return true;
		else return false;
	}
	
	/**
     * @return the maximum number of times a method will be retried
     */
    public int getRetryCount() {
        return this.retryCount;
    }

	
}
