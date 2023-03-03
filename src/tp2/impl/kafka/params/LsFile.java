package tp2.impl.kafka.params;

public class LsFile {

    private String userId;
    public String getUserId() {
        return userId;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public LsFile(String uid, String password){
        userId = uid;
        this.password = password;

    }

    
}
