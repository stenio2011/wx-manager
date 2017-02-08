package org.stenio.wx.thirdparty.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.stenio.wx.thirdparty.service.AuthService;


public class RefreshTokenJob {

	@Autowired
	private AuthService authService;

	public void doJob() {
		authService.autoRefreshAccessToken();
	}

}
