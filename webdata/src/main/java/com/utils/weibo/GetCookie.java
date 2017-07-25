package com.utils.weibo;

public class GetCookie {
	private static String pc_url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)&_=";
	private static String mobil_url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)&_=";
	private static Account[] accounts = new Account[]{new Account("13640209595", "wuyong097", "2204563997"),new Account("13640220793", "gongping683", "2205006863"),new Account("13640221116", "weipo51598", "2204892543")};
	public static void main(String[] args) {
		// 13640209595----wuyong097----dalunz嘿----2204563997
		// 13640220793----gongping683----天大花花----2205006863
		// 13640221116----weipo51598----手机用户2204892543----2204892543
		String cookie = SinaLogin();
		System.out.println(cookie);
	}

	public static String SinaLogin() {
		int i = (int) Math.round(Math.random() * 2);
		Account login = accounts[i];
		LoginPojo loginPojo = new LoginPojo();
		loginPojo.setUsername(login.getName());
		loginPojo.setPassword(login.getPassword());
		loginPojo.setUid(login.getUid());
		
		// 非代理登陆
		GetUserCookie cookieUtil = new GetUserCookie(loginPojo);
		String cookieString = "";
		try {
			cookieString = cookieUtil.getCookies("7.0");
			if (cookieString == null || cookieString.isEmpty()) {
				System.out.println(Thread.currentThread().getName() + " " + login.getName() + " 最终得到的cookie为空，请检查");
			} else {
				System.out.println(Thread.currentThread().getName() + " " + login.getName() + " 登录成功");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return cookieString;
	}

	public static class Account{
		private String name;
		private String password;
		private String uid;
		private boolean isFree;
		private int limit;
		public Account(String phone,String psw,String id){
			this.name = phone;
			this.password = psw;
			this.uid = id;
			this.isFree = true;
			this.limit = 1;
		}
		public String getName() {
			return name;
		}
		public String getPassword() {
			return password;
		}
		public String getUid() {
			return uid;
		}
	}
}
