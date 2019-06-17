package com.dili.ss.seata.boot;

import com.dili.ss.seata.consts.SeataConsts;
import io.seata.core.context.RootContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截每次http请求，判断header中是否有XID
 * 有则调用RootContext.bind(restXid)绑定全局事务
 */
public class SeataXidFilter extends OncePerRequestFilter {
    protected Logger logger = LoggerFactory.getLogger(SeataXidFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String xid = RootContext.getXID();
        String restXid = request.getHeader(SeataConsts.XID);
        boolean bind = false;
        if(StringUtils.isBlank(xid)&&StringUtils.isNotBlank(restXid)){
            RootContext.bind(restXid);
            bind = true;
            if (logger.isDebugEnabled()) {
                logger.debug("bind[" + restXid + "] to RootContext");
            }
        }
        try{
            filterChain.doFilter(request, response);
        } finally {
            if (bind) {
                String unbindXid = RootContext.unbind();
                if (logger.isDebugEnabled()) {
                    logger.debug("unbind[" + unbindXid + "] from RootContext");
                }
                if (!restXid.equalsIgnoreCase(unbindXid)) {
                    logger.warn("xid in change during http rest from " + restXid + " to " + unbindXid);
                    if (unbindXid != null) {
                        RootContext.bind(unbindXid);
                        logger.warn("bind [" + unbindXid + "] back to RootContext");
                    }
                }
            }
        }
    }
}
