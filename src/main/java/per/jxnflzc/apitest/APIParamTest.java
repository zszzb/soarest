package per.jxnflzc.apitest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

public class APIParamTest {
    public static void main(String [] args){
        Scanner scanner = new Scanner(System.in);
        String url = "http://v.juhe.cn/todayOnhistory/queryEvent.php";
        url += "?key=990f300a06bf92244b651afc029d72fc";
        System.out.print("你想查询今天的“历史上的今日”还是指定日期的？\n1.今天的\n2.指定日期的\n其他.退出\n请输入（1、2或其它内容）：");
        String choice = scanner.next();
        switch (choice){
            case "1":
                Calendar calendar = Calendar.getInstance();
                url += "&date=" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.DATE);
                break;
            case "2":
                System.out.print("请输入你想查询“历史上的今日”的日期（格式：月 日）：");
                String month = scanner.next();
                String day = scanner.next();
                url += "&date=" + month + "/" + day;
                break;
            default:System.exit(0);
        }
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(url, null, String.class);
        JSONObject jsonData = JSONObject.fromObject(response);
        JSONArray todayOnhistory = (JSONArray) jsonData.get("result");//修改不规范命名
        String qrText = "";
        for (int i = 0; i < todayOnhistory.size(); i++){
            JSONObject jsonObject = todayOnhistory.getJSONObject(i);
            System.out.println(jsonObject.getString("date") + ", " + jsonObject.getString("title"));
            if (i < 3){
                qrText += jsonObject.getString("date") + "," + jsonObject.getString("title");
            }
        }

        url = "http://apis.juhe.cn/qrcode/api";
        url += "?key=00c69899d29edf2312478018661ed9be";
        url += "&text=" + qrText;
        restTemplate = new RestTemplate();
        response = restTemplate.postForObject(url, null, String.class);
        jsonData = JSONObject.fromObject(response);
        JSONObject qrcode = (JSONObject) jsonData.get("result");//修改不规范命名
        String base64_image = qrcode.getString("base64_image");

        Base64.Decoder decoder = Base64.getDecoder();
        try {
            // Base64解码
            byte[] b = decoder.decode(base64_image);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream("test.jpg");
            out.write(b);
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }
}
