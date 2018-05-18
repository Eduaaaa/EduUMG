/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package redsocial;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ricardoi
 */
public class CarnetAPI {
    public static String consultar(String query,String request){
            String respuesta ="";
        try{
            String urlParameters  = "param1=a";            
            byte[] postData       = query.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;

            URL    url            = new URL( "https://firestore.googleapis.com/v1beta1/projects/circulo-umg/databases/(default)/"+request );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty( "Content-Type", "application/json"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( 
                    DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
               wr.write( postData );
            }
            InputStream _is;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                _is = conn.getInputStream();
            } else {
                 /* error from server */
                _is = conn.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((_is)));

            String output;
            while ((output = br.readLine()) != null) {
                respuesta = respuesta.concat(output);
            }
            

            conn.disconnect();
        }catch(Exception e){
            System.out.println("error:"+e.getMessage());
            respuesta="";
        }
        return respuesta;
    }
   
    
    public static String buscar(String carnet){
        String resultado ="";
        String query = "{structuredQuery: {where: {fieldFilter: {field: {fieldPath: \"carnet\"},value: {stringValue: \""+carnet+"\"},op: \"EQUAL\"}},from: [{collectionId: \"circulo\"}]}}";
        String respuesta = consultar(query,"documents:runQuery");
        JSONArray arrJson  = new JSONArray(respuesta);
        if(arrJson.length()>0){
            JSONObject objJson= arrJson.getJSONObject(0);
            if(objJson.has("document")){
                objJson = objJson.getJSONObject("document");
                objJson = objJson.getJSONObject("fields");
                objJson = objJson.getJSONObject("fbid");
                resultado = objJson.getString("stringValue");
            }else{
                resultado = "";
            }
        }
        return resultado;
    }
    
    public static boolean crear(String carnet){
        JSONObject objJson = new JSONObject();
        JSONObject campos = new JSONObject();
        JSONObject objCarnet = new JSONObject();
        JSONObject objFbid = new JSONObject();
        objCarnet.put("stringValue",carnet);
        campos.put("carnet",objCarnet);
        objFbid.put("stringValue", "9999999");
        campos.put("fbid",objFbid);
        objJson.put("fields",campos);
        String respuesta = consultar(campos.toString(),"documents/circulo");
        return true;
    }
    
}
