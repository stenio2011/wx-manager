package org.stenio.common.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by bjhexin3 on 2017/2/7.
 */
public class HttpClientTest {
    @Test
    public void get() throws Exception {
        String response = HttpClient.get("http://www.baidu.com");
        assertNotNull(response);
    }

    @Test
    public void get1() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("a", "1");
        String response = HttpClient.get("http://www.baidu.com", map);
        System.out.println(response);
        assertNotNull(response);
    }

    @Test
    public void get2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        map.put("a", "1");
        map.put("b", list);
        String response = HttpClient.get("http://www.baidu.com", map);
        System.out.println(response);
        assertNotNull(response);
    }

    @Test
    public void get3() throws Exception {

    }

    @Test
    public void postForm() throws Exception {

    }

    @Test
    public void postForm1() throws Exception {

    }

    @Test
    public void postForm2() throws Exception {

    }

    @Test
    public void postForm3() throws Exception {

    }

    @Test
    public void postForm4() throws Exception {

    }

    @Test
    public void postForm5() throws Exception {

    }

    @Test
    public void postJson() throws Exception {

    }

    @Test
    public void postJson1() throws Exception {

    }

    @Test
    public void postJson2() throws Exception {

    }

    @Test
    public void postJson3() throws Exception {

    }

    @Test
    public void postJson4() throws Exception {

    }

    @Test
    public void postJson5() throws Exception {

    }

}