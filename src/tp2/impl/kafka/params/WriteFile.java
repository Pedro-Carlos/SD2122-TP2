package tp2.impl.kafka.params;

public class WriteFile {
    private String filename;
    public String getFilename() {
        return filename;
    }

    private byte[] data;
    public byte[] getData() {
        return data;
    }

    private String userId;
    public String getUserId() {
        return userId;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public WriteFile(String filename, byte[] data, String uid, String password){
        this.filename = filename;
        this.data = data;
        userId = uid;
        this.password = password;

    }

    
}
