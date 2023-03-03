package tp2.impl.kafka.params;

public class DeleteUFiles {

    private String userId;
    public String getUserId() {
        return userId;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    private String token;
    public String getToken(){
        return token;
    }

    public DeleteUFiles(String uid, String password, String token){
        userId = uid;
        this.password = password;
        this.token = token;

    }

    
}
