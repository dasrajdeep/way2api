package way2api.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import way2api.model.Method;
import way2api.model.Request;
import way2api.model.Response;

/**
 * 
 * @author Rajdeep Das <das.rajdeep97@gmail.com>
 */
public class Navigator {
    
    private URL currentDomain=null;
    
    private String referer=null;
    
    private Map<String,String> cookies=new HashMap();
    
    private Properties config=null;
    
    /**
     * 
     * @param config the request configuration
     */
    public Navigator(Properties config) {
        this.config=config;
    }
    
    /**
     * 
     * @param request the built request
     * @return response
     */
    private List communicate(List request) {Utilities.dump(request);
        if(this.getCurrentDomain()==null) return null;
        
        List response=null;
        
        try {
            InetAddress host=InetAddress.getByName(this.getCurrentDomain().getHost());
            Socket socket=new Socket(host,80);
            
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            Scanner in=new Scanner(socket.getInputStream());
            
            for(Iterator i=request.iterator();i.hasNext();) out.println((String)i.next());
            out.println();
            out.flush();
            
            response=new ArrayList<String>();
            while(in.hasNextLine()) {
                String line=in.nextLine();
                response.add(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 
     * @param resource the resource path
     * @param parameters data to be sent
     * @return response
     */
    public Response get(String resource, Map parameters) {
        Request req=new Request(this.config);
        
        req.setMethod(Method.GET);
        req.setHost(this.getCurrentDomain().getHost());
        req.setResourcePath(resource);
        req.setParameters(parameters);
        
        if(this.getReferer()!=null) req.addHeader("Referer", getReferer());
        if(this.getCookies()!=null) req.addHeader("Cookie", getCookies());
        
        List request=req.buildRequest();
        List response=this.communicate(request);
        
        Response result=new Response(response);
        
        return result;
    }
    
    /**
     * 
     * @param resource the resource path
     * @param parameters data to be sent
     * @param multipart if multipart data is to be sent
     * @return response
     */
    public Response post(String resource, Map parameters, boolean multipart) {
        Request req=new Request(this.config);
        
        req.setMethod(Method.POST);
        req.setHost(this.getCurrentDomain().getHost());
        req.setResourcePath(resource);
        req.setParameters(parameters);
        req.setMultipart(multipart);
        
        if(this.getReferer()!=null) req.addHeader("Referer", getReferer());
        if(this.getCookies()!=null) req.addHeader("Cookie", getCookies());
        
        List request=req.buildRequest();
        List response=this.communicate(request);
        
        Response result=new Response(response);
        
        return result;
    }
    
    /**
     * 
     * @param cookie the cookie to add
     */
    public void addCookie(String cookie) {
        String pair[]=cookie.split("=");
        this.cookies.put(pair[0], pair[1]);
    }
    
    /**
     * 
     * @param cookie the cookie to remove
     */
    public void removeCookie(String cookie) {
        this.cookies.remove(cookie);
    }
    
    /**
     * 
     */
    public void removeAllCookies() {
        this.cookies.clear();
    }
    
    /**
     * @return the currentDomain
     */
    public URL getCurrentDomain() {
        return currentDomain;
    }

    /**
     * @param currentDomain the currentDomain to set
     */
    public void setCurrentDomain(URL currentDomain) {
        this.currentDomain = currentDomain;
    }

    /**
     * @return the referer
     */
    public String getReferer() {
        return referer;
    }

    /**
     * @param referer the referer to set
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * @return the cookie
     */
    public String getCookies() {
        String cookie="";
        
        for(Iterator i=this.cookies.keySet().iterator();i.hasNext();) {
            String key=(String)i.next();
            cookie+="; "+key+"="+cookies.get(key);
        }
        if(!cookie.isEmpty()) cookie=cookie.substring(2);
        else cookie=null;
        
        return cookie;
    }

    /**
     * @param cookies the cookies to set
     */
    public void setCookies(Map cookies) {
        this.cookies = cookies;
    }

    /**
     * @return the config
     */
    public Properties getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Properties config) {
        this.config = config;
    }
    
}
