package com.allan.springBootBoard.web.error.exception;

import com.allan.springBootBoard.web.error.code.ErrorCode;

public class BoardNotFoundException extends BusinessException{

    public BoardNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

}
