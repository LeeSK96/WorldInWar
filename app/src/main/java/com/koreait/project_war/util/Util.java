package com.koreait.project_war.util;

public interface Util {

    //192.168.1.6 학원
    //192.168.0.9

    static String IP = "192.168.102.9";
    static String SERVER_IP = "http://"+ IP +":9090/Project_war/node.jsp";

    static String SVR = IP;
    static String TYPE_REGI = "type_regi";
    static String TYPE_LOGIN = "type_login";
    static String SVR_ADDR = "http://" + SVR + ":9090/Project_war/login.jsp";

    static String SERVER_IP_TEST = "http://"+IP+":9090/Project_war/test.jsp";
    static String SERVER_IP_INSERT = "http://"+IP+":9090/Project_war/inven.jsp";
    static String SERVER_IP_INVEN = "http://"+IP+":9090/Project_war/inven_2.jsp";
    static String SERVER_IP_INVEN_3 = "http://"+IP+":9090/Project_war/inven_3.jsp";
    static String SERVER_IP_INVEN_4 = "http://"+IP+":9090/Project_war/inven_4.jsp";
    static String SERVER_IP_ATTACK = "http://"+IP+":9090/Project_war/attack.jsp";
    static String SERVER_IP_MARKET = "http://"+IP+":9090/Project_war/unit_market.jsp";
}
