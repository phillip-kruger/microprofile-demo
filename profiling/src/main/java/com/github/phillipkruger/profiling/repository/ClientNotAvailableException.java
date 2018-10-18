package com.github.phillipkruger.profiling.repository;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientNotAvailableException extends RuntimeException {

    public ClientNotAvailableException(String string) {
        super(string);
    }

    public ClientNotAvailableException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public ClientNotAvailableException(Throwable thrwbl) {
        super(thrwbl);
    }

    public ClientNotAvailableException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
    
}
