package com.github.phillipkruger.profiling.jaxrs;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import lombok.extern.java.Log;

@Log
@Provider
@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.TEXT_HTML,MediaType.TEXT_PLAIN})
public class UserEventReader implements MessageBodyReader<UserEvent>{
    
    @Inject 
    private UserEventConverter converter;
    
    @Override
    public boolean isReadable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return type == UserEvent.class;
    }

    @Override
    public UserEvent readFrom(Class<UserEvent> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, String> mm, InputStream in) throws IOException, WebApplicationException {
        return converter.fromJsonStream(in);
    }

}