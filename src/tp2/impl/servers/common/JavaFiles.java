package tp2.impl.servers.common;

import static tp2.api.service.java.Result.error;
import static tp2.api.service.java.Result.ok;
import static tp2.api.service.java.Result.ErrorCode.FORBIDDEN;
import static tp2.api.service.java.Result.ErrorCode.INTERNAL_ERROR;
import static tp2.api.service.java.Result.ErrorCode.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;

import tp2.api.service.java.Files;
import tp2.api.service.java.Result;
import util.IO;
import util.Token;
import util.Hash;


public class JavaFiles implements Files {
	static final String DELIMITER = "$$$";
	private static final String ROOT = "/tmp/";

	
	public JavaFiles() {
		new File( ROOT ).mkdirs();
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		String[] splited = token.split(JavaDirectory.DELIMITER);
		long time = splited[1].length() > 0 ? Long.parseLong(splited[1]) : 0;


		if ( !splited[0].equals(Hash.of(Token.get() + fileId + time)) || Token.Expired(time)) {
			return error( FORBIDDEN );
		}	

		fileId = fileId.replace( DELIMITER, "/");
		byte[] data = IO.read( new File( ROOT + fileId ));
		return data != null ? ok( data) : error( NOT_FOUND );
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		String[] splited = token.split(JavaDirectory.DELIMITER);
		long time = splited[1].length() > 0 ? Long.parseLong(splited[1]) : 0;


		if ( !splited[0].equals(Hash.of(Token.get() + fileId + time)) || Token.Expired(time)) {
			return error( FORBIDDEN );
		}	

		
		

		fileId = fileId.replace( DELIMITER, "/");
		boolean res = IO.delete( new File( ROOT + fileId ));	
		return res ? ok() : error( NOT_FOUND );
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		String[] splited = token.split(JavaDirectory.DELIMITER);
		long time = splited[1].length() > 0 ? Long.parseLong(splited[1]) : 0;


		if ( !splited[0].equals(Hash.of(Token.get() + fileId + time)) || Token.Expired(time)) {
			return error( FORBIDDEN );
		}	

		

		fileId = fileId.replace( DELIMITER, "/");
		File file = new File(ROOT + fileId);
		file.getParentFile().mkdirs();
		IO.write( file, data);
		return ok();
	}

	@Override
	public Result<Void> deleteUserFiles(String userId, String token) {
		String[] splited = token.split(JavaDirectory.DELIMITER);
		long time = splited[1].length() > 0 ? Long.parseLong(splited[1]) : 0;


		if ( !splited[0].equals(Hash.of(Token.get() + userId + time)) || Token.Expired(time)) {
			return error( FORBIDDEN );
		}	


		

		File file = new File(ROOT + userId);
		try {
			java.nio.file.Files.walk(file.toPath())
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
			return error(INTERNAL_ERROR);
		}
		return ok();
	}

	public static String fileId(String filename, String userId) {
		return userId + JavaFiles.DELIMITER + filename;
	}

}