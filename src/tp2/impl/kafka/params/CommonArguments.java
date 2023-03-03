package tp2.impl.kafka.params;

public class CommonArguments {
    
    private String filename;
    public String getFilename() {
        return filename;
    }

    private String userId;
    public String getUserId() {
        return userId;
    }


    private String userId2;
    public String getUserId2() {
        return userId2;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public CommonArguments(String filename, String uid, String uid2, String password){
        this.filename = filename;
        userId = uid;
        userId2 = uid2;
        this.password = password;
    }




}
