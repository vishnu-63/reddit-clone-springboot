package com.programming.techie.springredditclone.exceptions;

import com.programming.techie.springredditclone.dto.PostResponse;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException(String message) {
        super(message);
    }
}
