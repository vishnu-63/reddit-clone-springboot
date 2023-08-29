package com.programming.techie.springredditclone.exceptions;

import org.springframework.mail.MailException;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exMsg) {
        super(exMsg);
    }
}
