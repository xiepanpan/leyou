package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: xiepanpan
 * @Date: 2019/8/16
 * @Description:
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
