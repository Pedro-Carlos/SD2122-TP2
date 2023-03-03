package tp2.impl.servers.common;

import java.util.logging.Logger;

import tp2.api.service.rest.RestFiles;
import tp2.impl.clients.rest.DBClient;
import tp2.impl.servers.rest.RestResource;
import util.DBState;


public class JavaDropBox extends RestResource implements RestFiles{
	static final String DELIMITER = "$$$";
	private static final String ROOT = "/files";
  final DBClient dbClient;
	final static Logger Log = Logger.getLogger(JavaDropBox.class.getName());
	
	public JavaDropBox(boolean clear ,String apiKey, String apiSecret, String accessTokenStr) {
    dbClient = new DBClient(apiKey, apiSecret, accessTokenStr);

		if(!DBState.get()){
			dbClient.createFolder(ROOT);
		}
    if(clear){
      this.deleteAll(null);
			dbClient.createFolder(ROOT);
    }
		DBState.set();
	}

	
	public byte[] getFile(String fileId, String token) {
		fileId = fileId.replace( DELIMITER, "/");
    var res = dbClient.getFile(ROOT + "/" + fileId,null );
    return super.resultOrThrow(res);

	}

	
	public void deleteFile(String fileId, String token) {
		fileId = fileId.replace( DELIMITER, "/");
    var res = dbClient.deleteFile(ROOT + "/" + fileId ,null);
    super.resultOrThrow( res);
	}

	
	public void writeFile(String fileId, byte[] data, String token) {
		fileId = fileId.replace( DELIMITER, "/");
    var res = dbClient.writeFile(ROOT + "/" + fileId, data,null );
    super.resultOrThrow( res);
	}

	
	public void deleteUserFiles(String userId, String token) {
    var res = dbClient.deleteFile(ROOT + "/" + userId ,null);
    super.resultOrThrow( res);
	}

	public void deleteAll(String token) {
    var res = dbClient.deleteFile(ROOT,null);
    super.resultOrThrow( res);
	}
}
