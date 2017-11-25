package com.yalir.okhttp.exception;

/**
 * @author yakir
 * @function 自定义异常类
 */

public class OKHttpException extends Exception {
    private static final long serialVersionUID = 1L;
    /**
     * the server reture code
     */
    private int ecode;
    /**
     * the server return error message
     */
    private Object emsg;

    public OKHttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public void setEcode(int ecode) {
        this.ecode = ecode;
    }

    public Object getEmsg() {
        return emsg;
    }

    public void setEmsg(Object emsg) {
        this.emsg = emsg;
    }
}
