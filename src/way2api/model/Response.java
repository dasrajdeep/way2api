package way2api.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import way2api.core.Utilities;

/**
 * 
 * @author Rajdeep Das <das.rajdeep97@gmail.com>
 */
public class Response {
    
    private int statusCode=0;
    
    private Map<String,String> headers=new HashMap<String,String>();
    
    private List<String> responseData=null;
    
    private List<String> cookies=new ArrayList<String>();
    
    private boolean redirect=false;
    
    private boolean pageNotFound=false;
    
    private boolean ok=false;
    
    public Response() {}
    
    /**
     * 
     * @param response the response from the server
     */
    public Response(List response) {
        Iterator i=response.iterator();
        
        while(i.hasNext()) {
            String line=(String)i.next();
            
            if(line.isEmpty()) break;
            
            if(line.startsWith("HTTP")) {
                String[] parts=line.split("\\s+");
                this.statusCode=Integer.valueOf(parts[1]);
            } else {
                int div=line.indexOf(':');
                String key=line.substring(0, div);Utilities.dump(line);
                String value=line.substring(div+1).trim();
                
                if(key.equals("Set-Cookie")) {
                    value=value.substring(0, value.lastIndexOf(';'));
                    this.cookies.add(value);
                } else {
                    this.headers.put(key, value);
                }
            }
        }
        
        this.responseData=new ArrayList<String>();
        
        while(i.hasNext()) {
            this.responseData.add((String)i.next());
        }
        
        if(this.statusCode==200) this.ok=true;
        else if(this.statusCode==302) this.redirect=true;
        else if(this.statusCode==404) this.pageNotFound=true;
    }
    
    /**
     * 
     * @param header the header name
     * @return the header value
     */
    public String getHeaderValue(String header) {
        return this.getHeaders().get(header);
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
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
     * @return the responseData
     */
    public List<String> getResponseData() {
        return responseData;
    }

    /**
     * @param responseData the responseData to set
     */
    public void setResponseData(List<String> responseData) {
        this.responseData = responseData;
    }

    /**
     * @return the redirect
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * @param redirect the redirect to set
     */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    /**
     * @return the pageNotFound
     */
    public boolean isPageNotFound() {
        return pageNotFound;
    }

    /**
     * @param pageNotFound the pageNotFound to set
     */
    public void setPageNotFound(boolean pageNotFound) {
        this.pageNotFound = pageNotFound;
    }

    /**
     * @return the ok
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * @param ok the ok to set
     */
    public void setOk(boolean ok) {
        this.ok = ok;
    }

    /**
     * @return the cookies
     */
    public Map getCookies() {
        Map map=new HashMap();
        for(Iterator i=cookies.iterator();i.hasNext();) {
            String item=(String)i.next();
            String pair[]=item.split("=");
            map.put(pair[0], pair[1]);
        }
        return map;
    }

    /**
     * @param cookies the cookies to set
     */
    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }
    
}
