package way2api.core;

/**
 * 
 * @author Rajdeep Das
 */
public class Account {
    
    private String userName=null;
    
    private String password=null;
    
    private String senderName=null;
    
    public Account() {}
    
    public Account(String userName, String password) {
        this.userName=userName;
        this.password=password;
    }
    
    public Account(String userName, String password, String senderName) {
        this.userName=userName;
        this.password=password;
        this.senderName=senderName;
    }
    
    public String getUserName() {return this.userName;}
    
    public void setUserName(String userName) {this.userName=userName;}
    
    public String getPassword() {return this.password;}
    
    public void setPassword(String password) {this.password=password;}
    
    public String getSenderName() {return this.senderName;}
    
    public void setSenderName(String sender) {this.senderName=sender;}
    
}
