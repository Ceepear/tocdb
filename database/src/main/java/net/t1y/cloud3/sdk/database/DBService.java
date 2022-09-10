package net.t1y.cloud3.sdk.database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.t1y.cloud3.sdk.database.form.Callback;
import net.t1y.cloud3.sdk.database.form.Delete;
import net.t1y.cloud3.sdk.database.form.Insert;
import net.t1y.cloud3.sdk.database.form.Select;
import net.t1y.cloud3.sdk.database.form.Update;
import net.t1y.cloud3.sdk.external.defaults.HttpService;
import net.t1y.cloud3.sdk.util.Encrypt;

public final class DBService extends HttpService {
    private Encrypt.Encode encode;
    private Encrypt.Decode decode;
    private String[] keys;
    private String iv = "t1ivk4o9t1ivk4o9";
    private static final String url = "https://api.t1y.net/api/v3/";
    private static final String insert = "insert";
    private static final String delete = "delete";
    private static final String update = "update";
    private static final String select = "select";
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public DBService(String pKey, String sKey) {
        super(pKey, sKey);
        this.encode = Encrypt.getEncoder();
        this.decode = Encrypt.getDecoder();
        this.keys = getKey();
        setRequest(new RequestImp());
    }

    public DBService(String pKey, String sKey,Request request){
        this(pKey,sKey);
        setRequest(request);
    }

    public void insert(Insert insert,OnDBCallback onDBCallback){
        insert.setTimestamp(timestamp());
        String form = encode.aes(iv,Encrypt.AES_CBC_P7,keys[1],gson.toJson(insert));
        String p = data(form);
        request(url+DBService.insert,p,onDBCallback);
    }
    private void request(String url,String p,OnDBCallback onDBCallback){
        post(url,p, string -> {
            String s = decode.aes(iv,Encrypt.AES_CBC_P7,keys[1],string);
            if(s==null){
                Result result = new Result(){
                    @Override
                    public String getMsg() {
                        return string;
                    }
                    @Override
                    public String getData() {
                        return null;
                    }
                };
                onDBCallback.done(0,result);
            }
            else{
                Callback json = gson.fromJson(s, Callback.class);
                Result result = new Result(){
                    @Override
                    public String getMsg() {
                        return json.getMsg();
                    }
                    @Override
                    public Object getData() {
                        return json.getData();
                    }
                };
                onDBCallback.done(json.getCode(), result);
            }
        });
    }
    public void select(Select bean, OnDBCallback onDBCallback) {
        bean.setTimestamp(timestamp());
        String form = encode.aes(iv,Encrypt.AES_CBC_P7,keys[1],gson.toJson(bean));
        String p = data(form);
        String url = DBService.url +DBService.select;
        request(url,p,onDBCallback);
    }

    public void update(Update update,OnDBCallback onDBCallback){
        update.setTimestamp(timestamp());
        String form = encode.aes(iv,Encrypt.AES_CBC_P7,keys[1],gson.toJson(update));
        String p = data(form);
        String url = DBService.url +DBService.update;
        request(url,p,onDBCallback);
    }

    public void delete(Delete delete,OnDBCallback onDBCallback){
        delete.setTimestamp(timestamp());
        String form = encode.aes(iv,Encrypt.AES_CBC_P7,keys[1],gson.toJson(delete));
        String p = data(form);
        String url = DBService.url +DBService.delete;
        request(url,p,onDBCallback);
    }

    private String data(String form){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("key=").append(keys[0]).append("&");
        stringBuilder.append("data=").append(form).append("&");
        stringBuilder.append("signature=").append(encode.md5(keys[0]+form+keys[1],Encrypt.MD5_32));
        return stringBuilder.toString();
    }



    public interface OnDBCallback{
        void done(int code,Result result);
    }

    public interface Result{
        String getMsg();
        Object getData();
    }

    public interface ResultSync extends Result{
        int getCode();
    }

    public int timestamp(){
        return (int) (System.currentTimeMillis()/1000);
    }
}
