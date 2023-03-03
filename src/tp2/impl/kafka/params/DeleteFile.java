package tp2.impl.kafka.params;

public class DeleteFile {
    private String filename;
    public String getFilename() {
        return filename;
    }

    private String userId;
    public String getUserId() {
        return userId;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public DeleteFile(String filename, String uid, String password){
        this.filename = filename;
        userId = uid;
        this.password = password;

    }

    
}
