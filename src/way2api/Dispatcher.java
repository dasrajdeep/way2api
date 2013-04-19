package way2api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import way2api.core.*;
import way2api.model.Account;
import way2api.model.Response;

/**
 * 
 * @author Rajdeep Das
 * @version 1.0
 */
public class Dispatcher {
    
    private Account account=null;
    
    private Navigator nav=null;
    
    private Properties configuration;
    
    private String token=null;
    
    public Dispatcher() {
        this.bootstrap();
    }
    
    public Dispatcher(Account acc) {
        this.account=acc;
        this.bootstrap();
    }
    
    private void bootstrap() {
        Properties props=new Properties();
        try {
            InputStream file=this.getClass().getResourceAsStream("/resources/config.properties");
            props.load(file);
            this.configuration=props;
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        this.nav=new Navigator(this.configuration);
    }
    
    /**
     * 
     * @return successful or not 
     */
    public boolean connect() {
        try {
            URL url=new URL("http://www.way2sms.com/");
            nav.setCurrentDomain(url);
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        
        Response response=nav.get("", null);
        
        if(response.isRedirect()) {
            String loc=response.getHeaderValue("Location");
            
            try {
                URL url=new URL(loc);
                nav.setCurrentDomain(url);
            } catch(IOException e) {
                e.printStackTrace();
            }
            
            nav.setReferer(loc);
            response=nav.get("content/prehome.jsp", null);
            nav.setCookies(response.getCookies());
            
            return true;
        } else return false;
    }
    
    /**
     * 
     * @return Boolean
     */
    public boolean login() {
        if(this.getAccount()==null) return false;
        
        Map post=new HashMap<String,String>();
        post.put("username", this.getAccount().getUserName());
        post.put("password", this.getAccount().getPassword());
        post.put("userLogin", "no");
        post.put("button", "Login");
        
        nav.setReferer(nav.getCurrentDomain().toExternalForm()+"content/index.html");
        Response response=nav.post("w2sauth.action", post, false);
        
        if(response.isRedirect()) {
            String loc=response.getHeaderValue("Location"); 
            try {
                URL url=new URL(loc);
                this.token=url.getQuery().substring(3); 
                Map params=new HashMap();
                params.put("id", url.getQuery().substring(3));
                nav.get(url.getPath(), params);
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else return false;
    }
    
    /**
     * 
     * @return Boolean 
     */
    public boolean logout() {
        nav.setReferer(nav.getCurrentDomain().toExternalForm()+"Main.action?id="+this.token);
        
        Map params=new HashMap();
        params.put("folder", "inbox");
        
        nav.get("LogOut", params);
        
        return true;
    }
    
    /**
     * 
     * @param mobileNumber String
     * @param message String
     * @return Boolean
     */
    public boolean sendMessage(String mobileNumber, String message) {
        if(message.length()>140) return false;
        
        Map params=new HashMap();
        params.put("Token", this.token);
        
        nav.setReferer(nav.getCurrentDomain().toExternalForm()+"Main.action?id="+this.token);
        Response response=nav.get("jsp/SingleSMS.jsp", params);
        
        String m_15_b=this.findHtml(response.getResponseData(),"m_15_b");
        String t_15_k_5=this.findHtml(response.getResponseData(), "t_15_k_5");
        String diffNo=this.findHtml(response.getResponseData(), "diffNo");
        
        try {
            message=URLEncoder.encode(message, "utf-8");
            diffNo=URLEncoder.encode(diffNo, "utf-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        String nonce=".setAttribute(\"name\",";
        for(Iterator i=response.getResponseData().iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.indexOf(nonce)>0) {
                line=line.substring(line.indexOf(nonce)+nonce.length(), line.indexOf(')')).trim();
                nonce=line.substring(1, line.length()-1);
                break;
            }
        }
        
        String cookie="setCookie(\"";
        for(Iterator i=response.getResponseData().iterator();i.hasNext();) {
            String line=(String)i.next();
            int index=line.indexOf(cookie);
            if(index>0) {
                line=line.substring(index+cookie.length());
                cookie=line.substring(0, line.indexOf('"'));
                break;
            }
        }
        
        //if(!cookie.isEmpty()) nav.addCookie(cookie+"="+cookie);
        nav.addCookie("12489smssending34908=67547valdsvsikerexzc435457");
        
        Map post=new HashMap<String,String>();
        post.put("m_15_b", m_15_b);
        post.put(m_15_b, mobileNumber);
        post.put("t_15_k_5", t_15_k_5);
        post.put(t_15_k_5, this.token);
        post.put("i_m", "sndsms");
        post.put("txtLen", 140-message.length());
        post.put("textArea", message);
        post.put("kriya", this.findHtml(response.getResponseData(), "kriya"));
        post.put("chkall", "on");
        post.put("diffNo", diffNo);
        post.put(nonce, "");
        post.put("catnamedis", "Birthday");
        
        nav.setReferer(nav.getCurrentDomain().toExternalForm()+"jsp/SingleSMS.jsp?Token="+this.token);
        response=nav.post("jsp/sndsms2usr.action", post, false);
        if(response.isRedirect()) {
            String loc=response.getHeaderValue("Location");
            try {
                URL url=new URL(loc);
                nav.setCurrentDomain(url);
                nav.get(url.getPath(), null);
                return true;
            } catch(IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }
    
    /**
     * 
     * @param mobileNumber String
     * @param message String
     * @return Boolean
     */
    public boolean sendQuickMessage(String mobileNumber, String message) {
        if(!this.login()) return false;
        if(!this.sendMessage(mobileNumber, message)) return false;
        this.logout();
        return true;
    }
    
    private String findHtml(List response, String key) {
        String value="";
        
        for(Iterator i=response.iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.indexOf("name='"+key+"'")>0) {
                int start=line.indexOf("name='"+key+"'");
                line=line.substring(start);
                int val_id=line.indexOf("value=");
                
                if(val_id<0 || line.indexOf(">")<val_id) {
                    val_id=line.indexOf('>');
                    if(val_id<0) continue;
                    value=line.substring(val_id+1, line.indexOf('<', val_id));
                } else {
                    line=line.substring(val_id+6);
                    String tokens[]=line.split("\\s+");
                    value=tokens[0].substring(1, tokens[0].length()-1);
                }
            }
        }
        
        return value;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }
    
}
