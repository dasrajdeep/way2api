package way2api.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Rajdeep Das
 * @version 1.0
 */
public class Navigator {
    
    private URL host=null;
    
    private boolean loggedIn=false;
    
    private RequestBuilder builder=null;
    
    private ResponseHandler handler=null;
    
    public Navigator(RequestBuilder builder, ResponseHandler handler) {
        this.builder=builder;
        this.handler=handler;
        try {
            this.host=new URL("http://www.way2sms.com/");
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    public URL getHost() {return this.host;}
    
    public void setHost(URL host) {this.host=host;}
    
    public boolean isLoggedIn() {return this.loggedIn;}
    
    public void setLoggedIn(boolean logged) {this.loggedIn=logged;}
    
    /**
     * 
     * @param resp List
     * @return List
     */
    public List followRedirect(List resp) {
        List request=builder.buildBasicRequest(handler.getRedirectedLocation(resp)); 
        List response=handler.getResponse(this.host.getHost(), request, true); 
        return response;
    }
    
    /**
     * 
     * @param stage Integer
     * @param params Map
     * @return List
     */
    public List navigate(int stage, Map params) {
        List request=builder.buildRequest( stage, params);
        List response=handler.getResponse(this.host.getHost(), request, true);
        return response;
    }
    
    /**
     * 
     * @param location String
     * @return List
     */
    public List basicRequest(String location) {
        List request=builder.buildBasicRequest(location); 
        List response=handler.getResponse(this.host.getHost(), request, false); 
        return response;
    }
    
    /**
     * 
     * @param location List
     * @param postdata Map
     * @return List
     */
    public List basicPost(String location, Map postdata) {
        List request=builder.buildBasicPost(location, postdata);
        List response=handler.getResponse(this.host.getHost(), request, true);
        return response;
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
