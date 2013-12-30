package com.kodingen.cetrin.posttracker;

import android.text.format.Time;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PostTracker {
    private static final String TEST_GUID = "fcc8d9e1-b6f9-438f-9ac8-b67ab44391dd";
    public static final String SERVICE_URL = "http://services.ukrposhta.com/barcodestatistic/barcodestatistic.asmx/GetBarcodeInfo";

    public static BarcodeInfo track(String barcode, String lang) {
        return track(barcode, lang, TEST_GUID);
    }

    public static BarcodeInfo track(String barcode, String lang, String guid) {
        Map<String, String> data = postData(barcode, lang, guid);
        if (data.isEmpty()) {
            return null;
        }
        BarcodeInfo info = new BarcodeInfo();
        info.setBarcode(data.get("barcode"));
        info.setCode(data.get("code"));
        info.setLastOfficeIndex(data.get("lastofficeindex"));
        info.setLastOffice(data.get("lastoffice"));
        info.setEventDate(data.get("eventdate"));
        info.setEventDescription(data.get("eventdescription"));
        Time time = new Time();
        time.setToNow();
        info.setLastCheck(time.format("%c"));
        return info;
    }

    private static Map<String, String> postData(String barcode, String lang, String guid) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(SERVICE_URL);
        Map<String, String> data = new HashMap<String, String>();

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("guid", guid));
            nameValuePairs.add(new BasicNameValuePair("barcode", barcode));
            nameValuePairs.add(new BasicNameValuePair("culture", lang));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            InputStream is = response.getEntity().getContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();
            // get root node
            NodeList nodeList = doc.getElementsByTagName("BarcodeInfoService");
            Node node = nodeList.item(0);
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node temp = node.getChildNodes().item(i);
                data.put(temp.getNodeName(), temp.getTextContent());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
