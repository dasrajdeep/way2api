package way2api;

import way2api.model.Account;

public class Main {

    public static void main(String[] args) {
        
        Account acc=new Account("9051686531","retrograde");
        Dispatcher d=new Dispatcher(acc);
        
        d.connect();
        d.login();
        //d.sendMessage("9051686531", "updated");
        d.logout();
    }

}
