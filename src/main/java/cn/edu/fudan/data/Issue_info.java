package cn.edu.fudan.data;

import cn.edu.fudan.dao.InstanceDAO;
import cn.edu.fudan.dao.LocationDAO;
import cn.edu.fudan.entity.Instance;
import cn.edu.fudan.entity.Location;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class Issue_info {
    private static final InstanceDAO instanceDAO=new InstanceDAO();
    public static void toMap(String jsonStr,int commit_id) {
        // jsonString转换成Map
        Location first_location=new Location();
        List<Location> related=new ArrayList<>();

        Map<String, String> jsonMap = JSON.parseObject(jsonStr, new TypeReference<HashMap<String, String>>() {
        });
        Iterator it=jsonMap.keySet().iterator();
        while(it.hasNext()){
            String key;
            String value;
            key=it.next().toString();
            value=jsonMap.get(key);
            if(key=="issues"){
                Instance instance=new Instance();
                instance.setCommitId(commit_id);
                instance.setId(0);
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
//                            case "creationDate":instance.setCreationDate(entry.getValue().toString());break;
//                            case "updateDate":instance.setUpdateDate(entry.getValue().toString());break;
                            case "component":first_location.setComponent(entry.getValue().toString());break;
                            case "textRange": Location temp=LocationstoMap(entry.getValue().toString(),first_location);
                            first_location.setStartOffset(temp.getStartOffset());
                            first_location.setEndOffset(temp.getEndOffset());
                            first_location.setStartLine(temp.getStartLine());
                            first_location.setEndLine(temp.getEndLine());
                            break;
                            case "flows":
                                //System.out.println("\n");
                                List<Map<String,String>> listObjectFirFlows = (List<Map<String,String>>) JSONArray.parse(entry.getValue().toString());
                                for(Map<String,String> Flows : listObjectFirFlows){
                                    Location location=new Location();
                                    for (Map.Entry entryFlows : Flows.entrySet()){
                                        switch (entryFlows.getKey().toString()){
                                            case "locations":
                                                List<Map<String,String>> listObjectFirLocations = (List<Map<String,String>>) JSONArray.parse(entryFlows.getValue().toString());
                                                for(Map<String,String> Locations : listObjectFirLocations){
                                                    for(Map.Entry loc :Locations.entrySet()) {
                                                        //System.out.println("\nNULL:"+loc.getKey()+" "+loc.getValue().toString()+"\n");
                                                        switch (loc.getKey().toString()) {
                                                            case "component"://System.out.println("\nNULL:"+loc.getKey()+" "+loc.getValue().toString()+"\n");
                                                                location.setComponent(loc.getValue().toString());
                                                                break;
                                                            case "textRange"://System.out.println("\nNULL:"+loc.getKey()+" "+loc.getValue().toString()+"\n");
                                                                Location temp_loc= LocationstoMap(loc.getValue().toString(), location);
                                                                location.setStartOffset(temp_loc.getStartOffset());
                                                                location.setEndOffset(temp_loc.getEndOffset());
                                                                location.setStartLine(temp_loc.getStartLine());
                                                                location.setEndLine(temp_loc.getEndLine());
                                                                break;
                                                            default:;
                                                        }
                                                    }
                                                }
                                            default:;
                                        }
                                    }
                                    related.add(location);
                                }
                            default:;
                        }
                        //System.out.println( entry.getKey().toString()  + "  " +entry.getValue().toString());
                    }
                    //System.out.println("\n\n");
                    //if(instance.getSeverity()==null)System.out.println("error");
                    int inst_id=instanceDAO.insert(instance);
                    LocationDAO locationDAO=new LocationDAO();
                    first_location.setInstId(inst_id);
                    locationDAO.insert(first_location);
                    for(int i = 0; i<related.size(); i++) {
                        Location location=related.get(i);
                        location.setInstId(inst_id);
                        locationDAO.insert(location);
                    }
                    related.clear();
                }
            }
        }
    }

    public static Location LocationstoMap(String jsonStr,Location location) {
        // jsonString转换成Map
        Map<String, String> jsonMap = JSON.parseObject(jsonStr, new TypeReference<HashMap<String, String>>() {
        });
        Iterator it=jsonMap.keySet().iterator();
        while(it.hasNext()){
            String key;
            String value;
            key=it.next().toString();
            value=jsonMap.get(key);
            switch (key){
                case "startLine":location.setStartLine(Integer.parseInt(value));break;
                case "endLine":location.setEndLine(Integer.parseInt(value));break;
                case "startOffset":location.setStartOffset(Integer.parseInt(value));break;
                case "endOffset":location.setEndOffset(Integer.parseInt(value));break;
                default:;
            }
        }
        return location;
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

    private static void setGetPropertyUser(URLConnection connection) {
        String authString = "";
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("src/pjInfo.properties"));
            String account = properties.getProperty("sonar_account");
            String password = properties.getProperty("sonar_password");
            authString=account+':'+password;
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
    }
}
