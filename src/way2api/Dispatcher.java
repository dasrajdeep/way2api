package way2api;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import way2api.core.Account;
import way2api.core.Navigator;
import way2api.core.RequestBuilder;
import way2api.core.ResponseHandler;

/**
 * 
 * @author Rajdeep Das
 * @version 1.0
 */
public class Dispatcher {
    
    private Account account=null;
    
    private Navigator navigator=null;
    
    private RequestBuilder builder=null;
    
    private ResponseHandler handler=null;
    
    public Dispatcher() {
        this.bootstrap();
    }
    
    public Dispatcher(Account acc) {
        this.account=acc;
        this.bootstrap();
    }
    
    private void bootstrap() {
        this.builder=new RequestBuilder();
        this.handler=new ResponseHandler();
        
        this.navigator=new Navigator(this.builder,this.handler);
        builder.setHost(navigator.getHost().getHost());
        
        List basic=this.navigator.basicRequest(navigator.getHost().toExternalForm()); 
        if(handler.isRedirect(basic)) {
            
            String location=handler.getRedirectedLocation(basic);
            builder.addHeader("Referer", location);
            
            try {
                URL url=new URL(location);
                navigator.setHost(url);
                builder.setHost(url.getHost());
            } catch(Exception e) {
                e.printStackTrace();
            }
            
            List redirect=navigator.basicRequest(location+"content/prehome.jsp");
            String cookie=handler.extractCookie(redirect); 
            builder.addHeader("Cookie", cookie);
            
        }
    }
    
    public Account getAccount() {return this.account;}
    
    public void setAccount(Account acc) {this.account=acc;}
    
    /**
     * 
     * @return Boolean
     */
    public boolean login() {
        if(this.account==null) return false;
        
        builder.addHeader("Referer", navigator.getHost().toExternalForm()+"content/index.html");
        
        Map post=new HashMap<String,String>();
        post.put("username", this.account.getUserName());
        post.put("password", this.account.getPassword());
        post.put("userLogin", "no");
        post.put("button", "Login");
        
        List result=navigator.basicPost(navigator.getHost().toExternalForm()+"w2sauth.action", post);
        if(handler.isRedirect(result)) {
            String token=handler.extractToken(result); 
            builder.setToken(token);
            navigator.followRedirect(result); 
            navigator.setLoggedIn(true);
            return true;
        } else return false;
    }
    
    public boolean logout() {return false;}
    
    /**
     * 
     * @param mobileNumber String
     * @param message String
     * @return Boolean
     */
    public boolean sendMessage(String mobileNumber, String message) {
        if(!navigator.isLoggedIn()) return false;
        if(message.length()>140) return false;
        
        builder.addHeader("Referer", navigator.getHost().toExternalForm()+"Main.action?id="+builder.getToken());
        List sms=navigator.basicRequest(navigator.getHost().toExternalForm()+"jsp/SingleSMS.jsp?Token="+builder.getToken());
        
        builder.addHeader("Referer", navigator.getHost().toExternalForm()+"jsp/SingleSMS.jsp?Token="+builder.getToken());
        
        String m_15_b=this.findHtml(sms,"m_15_b");
        String t_15_k_5=this.findHtml(sms, "t_15_k_5");
        String diffNo=this.findHtml(sms, "diffNo");
        
        try {
            message=URLEncoder.encode(message, "utf-8");
            diffNo=URLEncoder.encode(diffNo, "utf-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        String nonce=".setAttribute(\"name\",";
        for(Iterator i=sms.iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.indexOf(nonce)>0) {
                line=line.substring(line.indexOf(nonce)+nonce.length(), line.indexOf(')')).trim();
                nonce=line.substring(1, line.length()-1);
                break;
            }
        }
        
        String cookie="setCookie(\"";
        for(Iterator i=sms.iterator();i.hasNext();) {
            String line=(String)i.next();
            int index=line.indexOf(cookie);
            if(index>0) {
                line=line.substring(index+cookie.length());
                cookie=line.substring(0, line.indexOf('"'));
                break;
            }
        }
        
        if(!cookie.isEmpty()) builder.addHeader("Cookie", builder.getHeader("Cookie")+"; "+cookie+"="+cookie);
        
        Map post=new HashMap<String,String>();
        post.put("m_15_b", m_15_b);
        post.put(m_15_b, mobileNumber);
        post.put("t_15_k_5", t_15_k_5);
        post.put(t_15_k_5, builder.getToken());
        post.put("i_m", "sndsms");
        post.put("txtLen", 140-message.length());
        post.put("textArea", message);
        post.put("kriya", this.findHtml(sms, "kriya"));
        post.put("chkall", "on");
        post.put("diffNo", diffNo);
        post.put(nonce, "");
        post.put("catnamedis", "Birthday");
        
        List sent=navigator.basicPost(navigator.getHost().toExternalForm()+"jsp/m2msms.action", post); 
        if(handler.isRedirect(sent)) navigator.followRedirect(sent);
        
        return true;
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
    
    private void dump(List data) {
        for(Iterator i=data.iterator();i.hasNext();) System.out.println(i.next());
        System.out.println();
    }
    
    private void dump(String data) {
        System.out.println(data);
        System.out.println();
    }
    
}
