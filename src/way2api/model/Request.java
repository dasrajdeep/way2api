package way2api.model;

import java.util.*;

/**
 * 
 * @author Rajdeep Das <das.rajdeep97@gmail.com>
 */
public class Request {
    
    private Method method=Method.GET;
    
    private String resourcePath="/";
    
    private String host=null;
    
    private Map<String,String> headers=null;
    
    private Map<String,Object> parameters=null;
    
    private boolean multipart=false;
    
    public Request() {}
    
    /**
     * 
     * @param cfg to specify configuration for request 
     */
    public Request(Properties cfg) {
        Enumeration keys=cfg.propertyNames();
        this.headers=new HashMap();
        while(keys.hasMoreElements()) {
            String key=(String)keys.nextElement();
            headers.put(key, cfg.getProperty(key));
        }
    }
    
    /**
     * 
     * @return the crafted request 
     */
    public List buildRequest() {
        List request=new ArrayList<String>();
        
        String params="";
        if(this.method==Method.GET) {
            String data="";
            for(Iterator i=this.getParameters().keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                data+="&"+key+"="+this.parameters.get(key);
            }
            if(!data.isEmpty()) params="?"+data.substring(1);
        }
        
        request.add(this.getMethod().toString()+" "+this.getResourcePath()+params+" HTTP/1.1");
        request.add("Host: "+this.getHost());
        
        for(Iterator i=this.getHeaders().keySet().iterator();i.hasNext();) {
            String key=(String)i.next();
            request.add(key+": "+getHeaders().get(key));
        }
        
        if(this.isMultipart()) {
            String boundary="---------------------------1298224831751237580694844359";
            int brkCount=2;
            String data="\n";
            for(Iterator i=this.getParameters().keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                data+=boundary+"\n";
                data+="Content-Disposition: form-data; name=\""+key+"\"\n";
                data+="\n";
                data+=(String)this.getParameters().get(key)+"\n";
                brkCount+=8;
            }
            data+=boundary+"--";
            
            request.add("Content-Type: multipart/form-data; boundary="+boundary);
            request.add("Content-Length: "+(data.length()+brkCount));
            request.add(data);
        } else if(this.method==Method.POST) {
            String data="";
            for(Iterator i=this.getParameters().keySet().iterator();i.hasNext();) {
                String key=(String)i.next();
                data+="&"+key+"="+this.parameters.get(key);
            }
            data=data.substring(1);
            request.add("Content-Length: "+data.length());
            request.add("");
            request.add(data);
        }
        
        return request;
    }
    
    /**
     * 
     * @param header the header name
     * @param value the header value
     */
    public void addHeader(String header, String value) {
        this.getHeaders().put(header, value);
    }
    
    /**
     * 
     * @param header the header name
     */
    public void removeHeader(String header) {
        this.getHeaders().remove(header);
    }
    
    /**
     * 
     * @param header the header name
     * @return the header value
     */
    public String getHeader(String header) {
        return this.getHeaders().get(header);
    }
    
    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath+= resourcePath;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the headers
     */
    public Map<String,String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String,String> headers) {
        this.headers = headers;
    }

    /**
     * @return the parameters
     */
    public Map<String,Object> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String,Object> parameters) {
        if(parameters!=null) this.parameters = parameters;
        else this.parameters=new HashMap();
    }

    /**
     * @return the multipart flag
     */
    public boolean isMultipart() {
        return multipart;
    }

    /**
     * @param multipart the multipart flag to set
     */
    public void setMultipart(boolean multipart) {
        this.multipart = multipart;
    }
    
}
