package net.t1y.cloud3.sdk.database;
import androidx.annotation.NonNull;
import net.t1y.cloud3.sdk.external.defaults.HttpService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestImp implements HttpService.Request {

    @Override
    public String post(String url, String parameter) {
        try {
            return post2(url,parameter);
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String get(String url) {
        return null;
    }
    private static int time = 10*1000;
    private static HttpURLConnection getConnection(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        TrustManager[] tm = {
                new X509TrustManager(){
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL url1 = new URL(null,url);
        HttpsURLConnection httpURLConnection = (HttpsURLConnection)url1.openConnection();
        httpURLConnection.setConnectTimeout(time);
        httpURLConnection.setSSLSocketFactory(ssf);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpURLConnection.setRequestProperty("accept-charset", "UTF-8");
        httpURLConnection.setRequestProperty("accept", "*/*");
        httpURLConnection.setReadTimeout(time);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        //httpURLConnection.setRequestProperty("user-agent", getUserAgent());
        return httpURLConnection;
    }
    private static String post2(String string,String param) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        HttpURLConnection httpURLConnection = getConnection(string);
        httpURLConnection.connect();
        param = URLEncoder.encode(param, "UTF-8").replace("%3D", "=").replace("%26", "&");
        os =  httpURLConnection.getOutputStream();
        if (param != null && param.length() > 0) {
            os.write(param.getBytes());
        }
        if (httpURLConnection.getResponseCode() == 200) {
            /** 通过连接对象获取一个输入流，向远程读取 */
            is = httpURLConnection.getInputStream();
            /** 封装输入流is，并指定字符集 */
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            /** 存放数据 */
            StringBuffer sbf = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sbf.append(line);
                sbf.append("\r\n");
            }
            result = sbf.toString();
            if (null != br) {
                br.close();
            }

            if (null != is) {
                is.close();
            }

            if (null != os) {
                os.close();
            }
            return result;
        }else{
            return null;
        }

        //System.gc();
    }

}
