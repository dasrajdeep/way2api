package way2api.core;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Rajdeep Das <das.rajdeep97@gmail.com>
 */
public class Utilities {
    
    public static void dump(String data) {
        System.out.println(data);
        System.out.println();
    }
    
    public static void dump(List data) {
        for(Iterator i=data.iterator();i.hasNext();) System.out.println(i.next());
        System.out.println();
    }
    
}
