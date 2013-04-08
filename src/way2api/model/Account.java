package way2api.model;

/**
 * 
 * @author Rajdeep Das <das.rajdeep97@gmail.com>
 */
public class Account {
    
    private String userName=null;
    
    private String password=null;
    
    private String senderName=null;
    
    public Account() {}
    
    /**
     * 
     * @param userName username of the sender
     * @param password password of the sender
     */
    public Account(String userName, String password) {
        this.userName=userName;
        this.password=password;
    }
    
    /**
     * 
     * @param userName username of sender
     * @param password password of sender
     * @param senderName name of sender
     */
    public Account(String userName, String password, String senderName) {
        this.userName=userName;
        this.password=password;
        this.senderName=senderName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
}
