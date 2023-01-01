package com.hyfd.yuanteUtils.sdk;



public class TencentApiException extends Exception{
	   private static final long serialVersionUID = 201709080000L;

	    private String            errCode;
	    private String            errMsg;

	    public TencentApiException() {
	        super();
	    }

	    public TencentApiException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public TencentApiException(String message) {
	        super(message);
	    }

	    public TencentApiException(Throwable cause) {
	        super(cause);
	    }

	    public TencentApiException(String errCode, String errMsg) {
	        super(errCode + ":" + errMsg);
	        this.errCode = errCode;
	        this.errMsg = errMsg;
	    }

	    public String getErrCode() {
	        return this.errCode;
	    }

	    public String getErrMsg() {
	        return this.errMsg;
	    }

}
