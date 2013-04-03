package way2api.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Rajdeep Das
 * @version 1.0
 */
public class ResponseHandler {
    
    private List lastResponse=null;
    
    public ResponseHandler() {}
    
    /**
     * 
     * @param hosturl String
     * @param request List
     * @param headersOnly Boolean
     * @return List
     */
    public List getResponse(String hosturl, List request, boolean headersOnly) {
        List response=new ArrayList<String>();
        
        try {
            InetAddress host=InetAddress.getByName(hosturl);
            Socket socket=new Socket(host,80);
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            for(Iterator i=request.iterator();i.hasNext();) out.println((String)i.next());
            out.println();
            out.flush();
            Scanner in=new Scanner(socket.getInputStream());
            while(in.hasNextLine()) {
                String line=in.nextLine();
                if(headersOnly && line.isEmpty()) break;
                response.add(line);
            }
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        
        this.lastResponse=response;
        
        return response;
    }
    
    public List getLastResponse() {return this.lastResponse;}
    
    /**
     * 
     * @param response List
     * @return Boolean
     */
    public boolean isRedirect(List response) {
        if(response.size()<3) return false;
        String header=(String)response.get(0);
        if(header.matches("(HTTP|http)/1.1 302 .*")) return true;
        else return false;
    }
    
    /**
     * 
     * @param response List
     * @return Boolean
     */
    public boolean isPageNotFound(List response) {
        if(response.size()<3) return false;
        String header=(String)response.get(0);
        if(header.matches("(HTTP|http)/1.1 404 .*")) return true;
        else return false;
    }
    
    /**
     * 
     * @param response List
     * @return String
     */
    public String getRedirectedLocation(List response) {
        String location=null;
        
        for(Iterator i=response.iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.startsWith("Location:")) {
                location=line.substring(line.indexOf("Location:")+9).trim();
                break;
            }
        }
        
        return location;
    }
    
    /**
     * 
     * @param response List
     * @return String
     */
    public String extractToken(List response) {
        String token=null;
        for(Iterator i=response.iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.indexOf("?id=")>0) {
                token=line.substring(line.indexOf("?id=")+4);
                break;
            }
        }
        return token;
    }
    
    /**
     * 
     * @param response List
     * @return String
     */
    public String extractCookie(List response) {
        String cookie=null;
        
        for(Iterator i=response.iterator();i.hasNext();) {
            String line=(String)i.next();
            if(line.startsWith("Set-Cookie:")) {
                cookie=line.substring(line.indexOf("Set-Cookie:")+11, line.indexOf(';')).trim();
                break;
            }
        }
        
        return cookie;
    }
    
}
