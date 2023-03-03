package tp2.impl.servers.rest;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import tp2.api.service.java.Directory;
import tp2.impl.servers.rest.util.CustomLoggingFilter;
import tp2.impl.servers.rest.util.GenericExceptionMapper;
import util.Token;

public class KafkaServer extends AbstractRestServer{
    
    public static final int PORT = 4567;
    private static Logger Log = Logger.getLogger(KafkaServer.class.getName());

    KafkaServer(){
        super(Log, Directory.SERVICE_NAME, PORT);
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register(new KafkaResource(0L));
        config.register(GenericExceptionMapper.class);
        config.register(CustomLoggingFilter.class);
    }
    
    public static void main(String[] args) throws Exception{
        Token.set( args.length == 0 ? "" : args[0] );
        new KafkaServer().start();
    }


}
