package way2api.core;

import java.net.URL;
import java.util.*;

/**
 * 
 * @author Rajdeep Das
 * @version 1.0
 */
public class RequestBuilder {
    
    private String resourcePath="/resources/";
    
    private String filePrefix="req_";
    
    private Map headers=new HashMap();
    
    private String host=null;
    
    private String token=null;
    
    public RequestBuilder() {}
    
    public RequestBuilder(String resourcePath, String prefix) {
        this.resourcePath=resourcePath;
        this.filePrefix=prefix;
    }
    
    public void setResourcePath(String path) {this.resourcePath=path;}
    
    public void setFilePrefix(String prefix) {this.filePrefix=prefix;}
    
    public void setHeaders(Map headers) {this.headers=headers;}
    
    public Map getHeaders() {return this.headers;}
    
    public String getHeader(String header) {return (String)this.headers.get(header);}
    
    /**
     * 
     * @param header String
     * @param value String
     */
    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }
    
    /**
     *  
     * @param header String
     */
    public void removeHeader(String header) {
        this.headers.remove(header);
    }
    
    public void setHost(String host) {this.host=host;}
    
    public void setToken(String token) {this.token=token;}
    
    public String getToken() {return this.token;}
    
    /**
     * 
     * @param index Integer
     * @param params Map
     * @return List
     */
    public List buildRequest(int index,Map params) {
        ArrayList<String> request=new ArrayList();
        Set keys=params.keySet();
        Scanner in=new Scanner(this.getClass().getResourceAsStream(this.resourcePath+this.filePrefix+index));
        
        while(in.hasNextLine()) {
            String line=in.nextLine();
            if(line.startsWith("Host")) line="Host: "+this.host;
            for(Iterator i=keys.iterator();i.hasNext();) {
                String key=(String)i.next();
                line=line.replaceAll(key+"=", key+"="+params.get(key));
                keys.remove(key);
            }
            request.add(line);
        }
        
        if(!headers.isEmpty()) {
            for(Iterator i=headers.keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                if(key==null) continue;
                request.add(key+": "+headers.get(key));
            }
        }
        
        return request;
    }
    
    /**
     * 
     * @param location String
     * @return List
     */
    public List buildBasicRequest(String location) {
        ArrayList<String> request=new ArrayList();
        Scanner in=new Scanner(this.getClass().getResourceAsStream(this.resourcePath+this.filePrefix+"basic"));
        
        while(in.hasNextLine()) {
            String line=in.nextLine();
            if(line.startsWith("Host")) line="Host: "+this.host;
            if(line.startsWith("GET")) {
                try {
                    URL u=new URL(location);
                    line="GET "+u.getPath();
                    if(u.getQuery()!=null && !u.getQuery().isEmpty()) line+="?"+u.getQuery();
                    line+=" HTTP/1.1";
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            request.add(line);
        }
        
        if(!headers.isEmpty()) {
            for(Iterator i=headers.keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                if(key==null) continue;
                request.add(key+": "+headers.get(key));
            }
        }
        
        return request;
    }
    
    /**
     * 
     * @param location String
     * @param postdata Map
     * @return List
     */
    public List buildBasicPost(String location, Map postdata) {
        ArrayList<String> request=new ArrayList();
        Scanner in=new Scanner(this.getClass().getResourceAsStream(this.resourcePath+this.filePrefix+"basic"));
        
        while(in.hasNextLine()) {
            String line=in.nextLine();
            if(line.startsWith("Host")) line="Host: "+this.host;
            if(line.startsWith("GET")) {
                try {
                    URL u=new URL(location);
                    line="POST "+u.getPath()+" HTTP/1.1";
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            request.add(line);
        }
        
        if(!headers.isEmpty()) {
            for(Iterator i=headers.keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                if(key==null) continue;
                request.add(key+": "+headers.get(key));
            }
        }
        
        String data="";
        Object[] keyset=postdata.keySet().toArray();
        for(int i=0;i<keyset.length;i++) {
            if(i>0) data+="&"+keyset[i]+"="+postdata.get(keyset[i]);
            else data+=keyset[i]+"="+postdata.get(keyset[i]);
        }
        
        request.add("Content-Type: application/x-www-form-urlencoded");
        request.add("Content-Length: "+data.length());
        request.add("");
        request.add(data);
        
        return request;
    }
    
}
