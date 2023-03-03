package tp2.impl.clients.rest;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;

import tp2.api.service.java.Files;
import tp2.api.service.java.Result;
import tp2.dropbox.msgs.CreateFileArgs;
import tp2.dropbox.msgs.CreateFolderV2Args;
import tp2.dropbox.msgs.FileArgs;
import tp2.tls.InsecureHostnameVerifier;

import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class DBClient implements Files{

  private static final String UPLOAD_FILE_URL = "https://content.dropboxapi.com/2/files/upload";
  private static final String CREATE_FOLDER_V2_URL = "https://api.dropboxapi.com/2/files/create_folder_v2";
  private static final String DELETE_V2_URL = "https://api.dropboxapi.com/2/files/delete_v2";
  private static final String DOWNLOAD_URL = "https://content.dropboxapi.com/2/files/download";


  private static final String CONTENT_TYPE_HDR = "Content-Type";
  private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
  protected static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";


  private final Gson json;
  private final OAuth20Service service;
  private final OAuth2AccessToken accessToken;
  final static Logger Log = Logger.getLogger(DBClient.class.getName());

  public DBClient(String apiKey, String apiSecret, String accessTokenStr) {
    HttpsURLConnection.setDefaultHostnameVerifier(new InsecureHostnameVerifier());

    json = new Gson();
    accessToken = new OAuth2AccessToken(accessTokenStr);
    service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);

  }

  public Result<Void> writeFile(String fileId, byte[] data, String token) {
    OAuthRequest createFile = new OAuthRequest(Verb.POST, UPLOAD_FILE_URL);
    createFile.addHeader("Content-Type", OCTET_STREAM_CONTENT_TYPE);
    createFile.addHeader("Dropbox-API-Arg", json.toJson(new CreateFileArgs(fileId, "overwrite",
        false, false, false)));

    createFile.setPayload(data);

    service.signRequest(accessToken, createFile);

    Response r;
    try {
      r = service.execute(createFile);
      if (r.getCode() == 409) {
        return Result.error(Result.ErrorCode.CONFLICT);
      } else if (r.getCode() == 200) {
        return Result.ok();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.error(Result.ErrorCode.INTERNAL_ERROR);

  }

  public Result<byte[]> getFile(String fileId, String token) {
    OAuthRequest getFile = new OAuthRequest(Verb.POST, DOWNLOAD_URL);
    getFile.addHeader("Content-Type", OCTET_STREAM_CONTENT_TYPE);
    getFile.addHeader("Dropbox-API-Arg", json.toJson(new FileArgs(fileId)));

    service.signRequest(accessToken, getFile);
    Response r;
    try {
      r = service.execute(getFile);
      if (r.getCode() == 409) {
        return Result.error(Result.ErrorCode.NOT_FOUND);
      } else if (r.getCode() == 200) {
        return Result.ok(r.getStream().readAllBytes());
      }
      Log.info("ERROR CODE " + r.getCode());

    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.error(Result.ErrorCode.INTERNAL_ERROR);
  }

  public Result<Void> deleteFile(String fileId, String token) {
    OAuthRequest delete = new OAuthRequest(Verb.POST, DELETE_V2_URL);
    delete.addHeader("Content-Type", JSON_CONTENT_TYPE);

    delete.setPayload(json.toJson(new FileArgs(fileId)));

    service.signRequest(accessToken, delete);

    Response r;
    try {
      r = service.execute(delete);
      if (r.getCode() == 404) {
        return Result.error(Result.ErrorCode.NOT_FOUND);
      } else if (r.getCode() == 200) {
        return Result.ok();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.error(Result.ErrorCode.INTERNAL_ERROR);

  }

  public Result<Void> deleteUserFiles(String userId, String token) {
    OAuthRequest delete = new OAuthRequest(Verb.POST, DELETE_V2_URL);
    delete.addHeader("Content-Type", JSON_CONTENT_TYPE);

    delete.setPayload(json.toJson(new FileArgs(userId)));

    service.signRequest(accessToken, delete);

    Response r;
    try {
      r = service.execute(delete);
      if (r.getCode() == 404) {
        return Result.error(Result.ErrorCode.NOT_FOUND);
      } else if (r.getCode() == 200) {
        return Result.ok();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.error(Result.ErrorCode.INTERNAL_ERROR);

  }

  public Result<Void> createFolder(String directoryName) {

    var createFolder = new OAuthRequest(Verb.POST, CREATE_FOLDER_V2_URL);
    createFolder.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);

    createFolder.setPayload(json.toJson(new CreateFolderV2Args(directoryName, false)));

    service.signRequest(accessToken, createFolder);

    Response r;
    try {
      r = service.execute(createFolder);
      if (r.getCode() == 409) {
        return Result.error(Result.ErrorCode.CONFLICT);
      } else if (r.getCode() == 200) {
        return Result.ok();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Result.error(Result.ErrorCode.INTERNAL_ERROR);

  }

}
