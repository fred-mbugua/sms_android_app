package com.example.dotpesa_new_dec_2022.utilities;

import com.example.dotpesa_new_dec_2022.MainActivity;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.NoHttpResponseException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLException;

public class HttpPOSTClient {


    public HttpPOSTClient(){

    }

    public FrameworkResponse getMisooCenterPostResponse(String urlPage, HashMap<String, String> hashmap){
        FrameworkResponse fr = new FrameworkResponse();
        CloseableHttpResponse httpResponse = null;
        try {

            //remove preceeding /
            urlPage = urlPage.substring(0, 1).contains("/") ? urlPage.substring(1) : urlPage;
            urlPage = urlPage.replaceAll("//", "/");

            String urlString = MainActivity.APPLICATION_MISOO_CENTER_HTTP_PROTOCOL + "://" + MainActivity.APPLICATION_MISOO_CENTER_HTTP_HOST_NAME + ":" + MainActivity.APPLICATION_MISOO_CENTER_HTTP_HOST_PORT + "/" + urlPage;
            URL url = new URL(urlString);
            System.out.println(urlString);
            HttpPost httpPost = new HttpPost(url.toURI());

            MultipartEntityBuilder entity = MultipartEntityBuilder.create();

            Set set = hashmap.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry mEntry = (Map.Entry) iterator.next();
                entity.addTextBody(String.valueOf(mEntry.getKey()), String.valueOf(mEntry.getValue()));
            }


            httpPost.setEntity(entity.build());

            System.out.println("dsadadsad");
            httpResponse = MainActivity.APPLICATION_MISOO_CENTER_HTTP_CLIENT.execute(httpPost, MainActivity.HTTP_SESSION_CONTEXT);
            System.out.println("asdsadsa");

            fr.setResponse(Boolean.TRUE, "Successfully posted to URL.",httpResponse);

        } catch (ClientProtocolException | SSLException cpe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to URL. Protocol"+cpe.getLocalizedMessage(),null);
        } catch (URISyntaxException | UnknownHostException | ConnectTimeoutException | NoHttpResponseException cpe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to URL. General"+cpe.getLocalizedMessage(),null);
        } catch (IOException ioe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to URL. IOE"+ioe.getLocalizedMessage(),null);
        }
        return fr;
    }

    public FrameworkResponse getLocalPostResponse(String urlPage, HashMap<String, String> hashmap){
        FrameworkResponse fr=new FrameworkResponse();
        CloseableHttpResponse httpResponse = null;
        try {

            //remove preceeding /
            urlPage = urlPage.substring(0, 1).contains("/") ? urlPage.substring(1) : urlPage;
            urlPage = urlPage.replaceAll("//", "/");

            String urlString = MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_PROTOCOL + "://" + MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_NAME + ":" + MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_HOST_PORT + "/" + urlPage;
            URL url = new URL(urlString);
            System.out.println(urlString);

            HttpPost httpPost = new HttpPost(url.toURI());

            MultipartEntityBuilder entity = MultipartEntityBuilder.create();

            Set set = hashmap.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry mEntry = (Map.Entry) iterator.next();
                entity.addTextBody(String.valueOf(mEntry.getKey()), String.valueOf(mEntry.getValue()));

            }

            httpPost.setEntity(entity.build());

            httpResponse = MainActivity.APPLICATION_LOCAL_MISOO_PORTAL_HTTP_CLIENT.execute(httpPost, MainActivity.HTTP_LOCAL_SESSION_CONTEXT);

            fr.setResponse(Boolean.TRUE, "Successfully loaded local to URL.",httpResponse);

        } catch (ClientProtocolException| HttpHostConnectException cpe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to local URL. Protocol"+cpe.getLocalizedMessage(),null);
            System.out.println(cpe.getLocalizedMessage() + " Client Protocol " );
        } catch (URISyntaxException| SocketException | SocketTimeoutException cpe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to local URL. Socket"+cpe.getLocalizedMessage(),null);
            System.out.println(cpe.getLocalizedMessage() + " URI Protocol ");
        } catch (IOException ioe) {
            fr.setResponse(Boolean.FALSE, "Failed to posted to local URL. IOE"+ioe.getLocalizedMessage(),null);
            System.out.println(ioe.getLocalizedMessage() + " IO error on login");
        }

        return fr;
    }


}


