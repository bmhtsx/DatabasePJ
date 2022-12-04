package cn.edu.fudan.data;

import cn.edu.fudan.dao.InstanceDAO;
import cn.edu.fudan.dao.LocationDAO;
import cn.edu.fudan.entity.Instance;
import cn.edu.fudan.entity.Location;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class issue_info {
    public static Map<String, String> toMap(String jsonStr,int commit_id) {
        // jsonString转换成Map
        Instance instance=new Instance();
        Location first_location=new Location();
        List<Location> related=new ArrayList<>();

        Map<String, String> jsonMapLocation;
        Iterator RangeIt;

        instance.setId(commit_id);

        Map<String, String> jsonMap = JSON.parseObject(jsonStr, new TypeReference<HashMap<String, String>>() {
        });
        Iterator it=jsonMap.keySet().iterator();
        while(it.hasNext()){
            String key;
            String value;
            key=it.next().toString();
            value=jsonMap.get(key);
            if(key=="issues"){
                List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(value);
                System.out.println("利用JSONArray中的parse方法来解析json数组字符串");
                for(Map<String,String> mapList : listObjectFir){
                    for (Map.Entry entry : mapList.entrySet()){
                        switch (entry.getKey().toString()){
                            case "severity":instance.setSeverity(entry.getValue().toString());break;
                            case "type":instance.setType(entry.getValue().toString());break;
                            case "status":instance.setStatus(entry.getValue().toString());break;
                            case "author":instance.setAuthor(entry.getValue().toString());break;
                            case "message":instance.setMessage(entry.getValue().toString());break;
                            case "creationDate":instance.setCreationDate(entry.getValue().toString());break;
                            case "updateDate":instance.setUpdateDate(entry.getValue().toString());break;
                            case "component":first_location.setComponent(entry.getValue().toString());break;
                            case "textRange":
                                jsonMapLocation = JSON.parseObject(entry.getValue().toString(), new TypeReference<HashMap<String, String>>() {
                                });
                                RangeIt=jsonMapLocation.keySet().iterator();
                                while(RangeIt.hasNext()){
                                    String RangeKey;
                                    String RangeValue;
                                    RangeKey=RangeIt.next().toString();
                                    RangeValue=jsonMapLocation.get(key);
                                    switch (RangeKey){
                                        case "startLine":first_location.setStartLine(Integer.parseInt(RangeValue));break;
                                        case "endLine":first_location.setEndLine(Integer.parseInt(RangeValue));break;
                                        case "startOffset":first_location.setStartOffset(Integer.parseInt(RangeValue));break;
                                        case "endOffset":first_location.setEndOffset(Integer.parseInt(RangeValue));break;
                                        default:;
                                    }
                                }
                                break;
                            case "flows":
                                List<Map<String,String>> listObjectFirFlows = (List<Map<String,String>>) JSONArray.parse(value);
                                for(Map<String,String> Flows : listObjectFirFlows){
                                    Location location=new Location();
                                    for (Map.Entry entryFlows : Flows.entrySet()){
                                        switch (entryFlows.toString()){
                                            case "component":location.setComponent(entry.getValue().toString());break;
                                            case "textRange":
                                                jsonMapLocation = JSON.parseObject(entry.getValue().toString(), new TypeReference<HashMap<String, String>>() {
                                                });
                                                RangeIt=jsonMapLocation.keySet().iterator();
                                                while(RangeIt.hasNext()){
                                                    String RangeKey;
                                                    String RangeValue;
                                                    RangeKey=RangeIt.next().toString();
                                                    RangeValue=jsonMapLocation.get(key);
                                                    switch (RangeKey){
                                                        case "startLine":location.setStartLine(Integer.parseInt(RangeValue));break;
                                                        case "endLine":location.setEndLine(Integer.parseInt(RangeValue));break;
                                                        case "startOffset":location.setStartOffset(Integer.parseInt(RangeValue));break;
                                                        case "endOffset":location.setEndOffset(Integer.parseInt(RangeValue));break;
                                                        default:;
                                                    }
                                                }
                                                break;
                                            default:
                                        }
                                    }
                                    related.add(location);
                                }
                            default:;
                        }
                        System.out.println( entry.getKey()  + "  " +entry.getValue());
                    }
                    System.out.println("\n\n");
                }
            }
        }
        InstanceDAO instanceDAO=new InstanceDAO();
        int inst_id=instanceDAO.insert(instance);
        LocationDAO locationDAO=new LocationDAO();
        first_location.setInstId(inst_id);
        locationDAO.insert(first_location);
        for(int i = 0; i<related.size(); i++) {
            Location location=related.get(i);
            location.setInstId(inst_id);
            locationDAO.insert(location);
        }
        return jsonMap;
    }

    public static String httpGet(String url){
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            setGetPropertyUser(connection);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            if (in != null){ try { in.close(); }catch(Exception e2){} }
        }
        return result.toString();
    }

    private static void setGetPropertyUser(@NotNull URLConnection connection) {
//        connection.setRequestProperty("accept", "*/*");
//        connection.setRequestProperty("connection", "Keep-Alive");
//        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        String authString = "admin:Yuchen0nl1ne";
        byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
    }
}
