package tp2.impl.servers.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import tp2.api.service.java.Files;
import tp2.impl.servers.common.JavaDropBox;
import tp2.impl.servers.rest.util.GenericExceptionMapper;
import util.Debug;

public class DropboxServer extends AbstractRestServer {
	public static final int PORT = 8080;

	private static Logger Log = Logger.getLogger(DropboxServer.class.getName());

	static boolean clear;
	static String apiKey;
	static String apiSecret;
	static String accessTokenStr;
	DropboxServer() {
		super(Log, Files.SERVICE_NAME, PORT);
	}

	@Override
	void registerResources(ResourceConfig config) {
		config.register(new JavaDropBox(clear, apiKey, apiSecret, accessTokenStr));
		config.register(GenericExceptionMapper.class);
		// config.register( CustomLoggingFilter.class);
	}

	public static void main(String[] args) throws Exception {

		Debug.setLogLevel(Level.INFO, Debug.TP2);
		clear = Boolean.parseBoolean(args[0]);
		apiKey = args[1];
		apiSecret = args[2];
		accessTokenStr = args[3];

		new DropboxServer().start();
	}
}