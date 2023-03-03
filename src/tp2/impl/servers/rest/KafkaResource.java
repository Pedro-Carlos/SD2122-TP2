package tp2.impl.servers.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.google.gson.Gson;

import tp2.api.FileInfo;
import tp2.api.service.java.Directory;
import tp2.api.service.java.Result;
import tp2.api.service.rest.RestDirectory;
import tp2.impl.kafka.KafkaPublisher;
import tp2.impl.kafka.KafkaSubscriber;
import tp2.impl.kafka.RecordProcessor;
import tp2.impl.kafka.params.CommonArguments;
import tp2.impl.kafka.params.DeleteFile;
import tp2.impl.kafka.params.DeleteUFiles;
import tp2.impl.kafka.params.LsFile;
import tp2.impl.kafka.params.WriteFile;
import tp2.impl.kafka.sync.SyncPoint;
import tp2.impl.servers.common.JavaDirectory;


public class KafkaResource extends RestResource implements RestDirectory{

    private static Logger Log = Logger.getLogger(KafkaResource.class.getName());
    private static String TOPIC = "kafka-rep";
    static int MAX_NUM_THREADS = 2;
    String secret;
    private static KafkaPublisher kp;
    private static KafkaSubscriber ks;
    Gson gson = new Gson();
    private String broker;
    private Long version;
    private SyncPoint syncPoint;
    final Directory dir;

    public KafkaResource(long v) {
        super();
        this.version = v;
        dir = new JavaDirectory();
        genTopic();
        this.syncPoint = new SyncPoint<>();
            ks.start(false, new RecordProcessor() {

                @Override
                public void onReceive(ConsumerRecord<String, String> r) {
                    long offset = r.offset();
                    String params = r.value();
                    Result res = null;
                    switch (r.key()){
                        case "writeFile" ->{
                            WriteFile wf = gson.fromJson(params, WriteFile.class);
                            res = dir.writeFile(wf.getFilename(), wf.getData(), wf.getUserId(), wf.getPassword());
                        }
                        case "deleteFile" ->{
                            DeleteFile df = gson.fromJson(params, DeleteFile.class);
                            res = dir.deleteFile(df.getFilename(), df.getUserId(), df.getPassword());
                        }
                        case "shareFile" -> {
                            CommonArguments ca = gson.fromJson(params, CommonArguments.class);
                            res = dir.shareFile(ca.getFilename(), ca.getUserId(), ca.getUserId2(), ca.getPassword());
                        }
                        case "unshareFile" -> {
                            CommonArguments ca = gson.fromJson(params, CommonArguments.class);
                            res = dir.unshareFile(ca.getFilename(), ca.getUserId(), ca.getUserId2(), ca.getPassword());
                        }
                        case "getFile" -> {
                            CommonArguments ca = gson.fromJson(params, CommonArguments.class);
                            res = dir.getFile(ca.getFilename(), ca.getUserId(), ca.getUserId2(), ca.getPassword());
                        }
                        case "lsFile" -> {
                            LsFile ls = gson.fromJson(params, LsFile.class);
                            res = dir.lsFile(ls.getUserId(), ls.getPassword());
                        }
                        case "deleteUserFiles" -> {
                            DeleteUFiles dfu = gson.fromJson(params, DeleteUFiles.class);
                            res = dir.deleteUserFiles(dfu.getUserId(), dfu.getPassword(), dfu.getToken());
                        }
                        }
                        version = offset;
                        syncPoint.setResult(offset, res);
                    }
                }
                
            );
        }
        //TODO Auto-generated constructor stub

    private <T> Result<T> publish(Long version, String op, String val){
        if(this.version == null){
            this.version =(0L);
        }

        long offset = kp.publish(TOPIC, op, val);
        this.version = offset;
        if(version != null){
            syncPoint.waitForVersion(version, 500);
        }
        
        return (Result<T>) syncPoint.waitForResult(offset);
    }


    private void genTopic(){
        broker = "kafka:9092";
        kp = KafkaPublisher.createPublisher(broker);
        ks = KafkaSubscriber.createSubscriber(broker, List.of(TOPIC), "earliest");
       }

    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
		Log.info(String.format("REST writeFile: filename = %s, data.length = %d, userId = %s, password = %s \n",
				filename, data.length, userId, password));
        String value = gson.toJson(new WriteFile(filename, data, userId, password));
        Result<FileInfo> result = publish(version, "writeFile", value);
		return super.resultOrThrow(result);
	}

	@Override
	public void deleteFile(String filename, String userId, String password) {
		Log.info(String.format("REST deleteFile: filename = %s, userId = %s, password =%s\n", filename, userId,
				password));
        String value = gson.toJson(new DeleteFile(filename, userId, password));
        Result<String> result = publish(version, "deleteFile", value);
		super.resultOrThrow(result);
	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password) {
		Log.info(String.format("REST shareFile: filename = %s, userId = %s, userIdShare = %s, password =%s\n", filename,
				userId, userIdShare, password));
        String value = gson.toJson(new CommonArguments(filename, userId, userIdShare, password));
		Result<String> result = publish(version, "shareFile", value);
        super.resultOrThrow(result);
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password) {
		Log.info(String.format("REST unshareFile: filename = %s, userId = %s, userIdShare = %s, password =%s\n",
				filename, userId, userIdShare, password));
                String value = gson.toJson(new CommonArguments(filename, userId, userIdShare, password));
                Result<String> result = publish(version, "unshareFile", value);
                super.resultOrThrow(result);
	}

	@Override
	public byte[] getFile(String filename, String userId, String accUserId, String password) {
		Log.info(String.format("REST getFile: filename = %s, userId = %s, accUserId = %s, password =%s\n", filename,
			userId, accUserId, password));
            String value = gson.toJson(new CommonArguments(filename, userId, accUserId, password));
            Result<byte[]> result = publish(version, "getFile", value);
        return super.resultOrThrow(result);

	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) {
		long T0 = System.currentTimeMillis();
		try {

			Log.info(String.format("REST lsFile: userId = %s, password = %s\n", userId, password));
            String value = gson.toJson(new LsFile(userId, password));
            Result<List<FileInfo>> result = publish(version, "lsFile", value);
			return super.resultOrThrow(result);
		} finally {
			System.err.println("TOOK:" + (System.currentTimeMillis() - T0));
		}
	}

	@Override
	public void deleteUserFiles(String userId, String password, String token) {
		Log.info(
				String.format("REST deleteUserFiles: user = %s, password = %s, token = %s\n", userId, password, token));
        String value = gson.toJson(new DeleteUFiles(userId, password, token));
        Result<String> result = publish(version, "deleteUserFiles", value);
		super.resultOrThrow(result);
	}
}
