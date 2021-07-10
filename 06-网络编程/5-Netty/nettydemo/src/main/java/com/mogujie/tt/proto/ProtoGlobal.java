package com.mogujie.tt.proto;

public class ProtoGlobal {

    public static final short CLIENT_PROTO = 0x3BB3;
    public static final int SVC_SVR = 0;
    public static final int SVC_ACC = 1;
    public static final byte SVC_ATH = 2;
    public static final byte SVC_ORG = 3;
    public static final char SVC_P2P = 4;
    public static final char SVC_GRP = 5;
    public static final int SVC_SMS = 6;
    public static final byte SVC_RES = 7;
    public static final int SVC_PSH = 8;
    public static final int SVC_GTW = 9;
    public static final int SVC_NETDISK = 10;
    public static final int SVC_CLI = 20;
    public static final int SVC_PC = 21;
    public static final int SVC_MB = 22;
    public static final int SVC_WEB = 23;
    public static final int SVC_VIDEO = 15;

    public static final int SVC_PUSH = 24;
    public static final int SVC_FILE_SAVE = 25;
    public static final int SVC_SYS = 255;
    public static final int SVC_MAX = 256;


    public static final int ACC_REQ_REGISTER = ((SVC_ACC << 8) | 0x1);
    public static final int ACC_RSP_REGISTER = ((SVC_ACC << 8) | 0x81);
    public static final int ACC_REQ_HEARTBEAT = ((SVC_ACC << 8) | 0x2);
    public static final int ACC_RSP_HEARTBEAT = ((SVC_ACC << 8) | 0x82);
    public static final int ACC_REQ_ADD_GRP = ((SVC_ACC << 8) | 0x3);
    public static final int ACC_RSP_ADD_GRP = ((SVC_ACC << 8) | 0x83);
    public static final int ACC_REQ_DEL_GRP = ((SVC_ACC << 8) | 0x4);
    public static final int ACC_RSP_DEL_GRP = ((SVC_ACC << 8) | 0x84);
    public static final int ACC_REQ_ADD_GRPUSER = ((SVC_ACC << 8) | 0x5);
    public static final int ACC_RSP_ADD_GRPUSER = ((SVC_ACC << 8) | 0x85);
    public static final int ACC_REQ_DEL_GRPUSER = ((SVC_ACC << 8) | 0x6);
    public static final int ACC_RSP_DEL_GRPUSER = ((SVC_ACC << 8) | 0x86);
    public static final int ACC_REQ_SYN_ADD_GRP_USERS = ((SVC_ACC << 8) | 0x7);
    public static final int ACC_RSP_SYN_ADD_GRP_USERS = ((SVC_ACC << 8) | 0x87);
    public static final int ACC_REQ_SYN_DEL_GRP_USERS = ((SVC_ACC << 8) | 0x8);
    public static final int ACC_RSP_SYN_DEL_GRP_USERS = ((SVC_ACC << 8) | 0x88);
    public static final int ACC_REQ_SYN_DEL_GRP = ((SVC_ACC << 8) | 0x9);
    public static final int ACC_RSP_SYN_DEL_GRP = ((SVC_ACC << 8) | 0x89);
    public static final int ACC_REQ_SVC_BROCAST = ((SVC_ACC << 8) | 0xa);
    public static final int ACC_RSP_SVC_BROCAST = ((SVC_ACC << 8) | 0x8a);

    public static final short AUT_REQ_LOGIN = ((SVC_ATH << 8) | 0x1);
    public static final int AUT_RSP_LOGIN = ((SVC_ATH << 8) | 0x81);
    public static final short AUT_REQ_LOGOUT = ((SVC_ATH << 8) | 0x2);
    public static final short AUT_RSP_LOGOUT = ((SVC_ATH << 8) | 0x82);
    public static final int AUT_REQ_KICKOFF = ((SVC_ATH << 8) | 0x3);
    public static final int AUT_RSP_KICKOFF = ((SVC_ATH << 8) | 0x83);
    public static final short AUT_REQ_USER_INFO = ((SVC_ATH << 8) | 0x4);
    public static final short AUT_RSP_USER_INFO = ((SVC_ATH << 8) | 0x84);
    public static final int AUT_REQ_MOD_USER = ((SVC_ATH << 8) | 0x5);
    public static final int AUT_RSP_MOD_USER = ((SVC_ATH << 8) | 0x85);
    public static final int AUT_REQ_MOD_STATU = ((SVC_ATH << 8) | 0x6);
    public static final int AUT_RSP_MOD_STATU = ((SVC_ATH << 8) | 0x86);

    public static final int AUT_RSP_USER_STATU = ((SVC_ATH << 8) | 0x87);
    public static final int AUT_REQ_ONLINECOUNT = ((SVC_ATH << 8) | 0x8);
    public static final int AUT_RSP_ONLINECOUNT = ((SVC_ATH << 8) | 0x88);
    public static final int AUT_REQ_ONLINE_USERS = ((SVC_ATH << 8) | 0x9);
    public static final int AUT_RSP_ONLINE_USERS = ((SVC_ATH << 8) | 0x89);

    public static final int AUT_REQ_SUB_STATU = ((SVC_ATH << 8) | 0xb);
    public static final int AUT_RSP_SUB_STATU = ((SVC_ATH << 8) | 0x8b);
    public static final int AUT_REQ_UNSUB_STATU = ((SVC_ATH << 8) | 0xc);
    public static final int AUT_RSP_UNSUB_STATU = ((SVC_ATH << 8) | 0x8c);
    public static final int AUT_REQ_PUB_STATU = ((SVC_ATH << 8) | 0xd);
    public static final int AUT_RSP_PUB_STATU = ((SVC_ATH << 8) | 0x8d);
    public static final short AUT_REQ_RCNT_SSIONS = ((SVC_ATH << 8) | 0xe);
    public static final int AUT_RSP_RCNT_SSIONS = ((SVC_ATH << 8) | 0x8e);
    public static final int AUT_REQ_P2P_READED_IDX = ((SVC_ATH << 8) | 0xf);
    public static final int AUT_RSP_P2P_READED_IDX = ((SVC_ATH << 8) | 0x8f);
    public static final int AUT_REQ_GRP_READED_IDX = ((SVC_ATH << 8) | 0x10);
    public static final int AUT_RSP_GRP_READED_IDX = ((SVC_ATH << 8) | 0x90);

    public static final int ORG_REQ_DPT_INFO = ((SVC_ORG << 8) | 0x1);
    public static final int ORG_RSP_DPT_INFO = ((SVC_ORG << 8) | 0x81);
    public static final short ORG_REQ_DPT_CHILDS = ((SVC_ORG << 8) | 0x2);
    public static final int ORG_RSP_DPT_CHILDS = ((SVC_ORG << 8) | 0x82);
    public static final short ORG_REQ_DPT_MEMBERS = ((SVC_ORG << 8) | 0x3);
    public static final short ORG_RSP_DPT_MEMBERS = ((SVC_ORG << 8) | 0x83);
    public static final int ORG_REQ_TOP_SSION = ((SVC_ORG << 8) | 0x5);
    public static final int ORG_RSP_TOP_SSION = ((SVC_ORG << 8) | 0x85);
    public static final int ORG_REQ_SCH_USER = ((SVC_ORG << 8) | 0x6);
    public static final int ORG_RSP_SCH_USER = ((SVC_ORG << 8) | 0x86);
    public static final int ORG_REQ_ADD_BLACK = ((SVC_ORG << 8) | 0x7);
    public static final int ORG_RSP_ADD_BLACK = ((SVC_ORG << 8) | 0x87);
    public static final int ORG_REQ_DEL_BLACK = ((SVC_ORG << 8) | 0x8);
    public static final int ORG_RSP_DEL_BLACK = ((SVC_ORG << 8) | 0x88);
    public static final int ORG_REQ_LIST_BLACK = ((SVC_ORG << 8) | 0x9);
    public static final int ORG_RSP_LIST_BLACK = ((SVC_ORG << 8) | 0x89);
    public static final int ORG_REQ_DPT_USERS = ((SVC_ORG << 8) | 0xa);
    public static final int ORG_RSP_DPT_USERS = ((SVC_ORG << 8) | 0x8a);
    public static final short ORG_REQ_GRP_LIST = ((SVC_ORG << 8) | 0xb);
    public static final short ORG_RSP_GRP_LIST = ((SVC_ORG << 8) | 0x8b);
    public static final int ORG_REQ_GRP_INFO = ((SVC_ORG << 8) | 0xc);
    public static final int ORG_RSP_GRP_INFO = ((SVC_ORG << 8) | 0x8c);
    public static final int ORG_REQ_GRP_LIST_MEMBER = ((SVC_ORG << 8) | 0xd);
    public static final int ORG_RSP_GRP_LIST_MEMBER = ((SVC_ORG << 8) | 0x8d);
    public static final int ORG_REQ_ADD_GRP = ((SVC_ORG << 8) | 0xe);
    public static final int ORG_RSP_ADD_GRP = ((SVC_ORG << 8) | 0x8e);
    public static final int ORG_REQ_MOD_GRP = ((SVC_ORG << 8) | 0xf);
    public static final int ORG_RSP_MOD_GRP = ((SVC_ORG << 8) | 0x8f);
    public static final int ORG_REQ_DEL_GRP = ((SVC_ORG << 8) | 0x10);
    public static final int ORG_RSP_DEL_GRP = ((SVC_ORG << 8) | 0x90);
    public static final int ORG_REQ_ADD_GRPMEMBER = ((SVC_ORG << 8) | 0x11);
    public static final int ORG_RSP_ADD_GRPMEMBER = ((SVC_ORG << 8) | 0x91);
    public static final int ORG_REQ_DEL_GRPMEMBER = ((SVC_ORG << 8) | 0x12);
    public static final int ORG_RSP_DEL_GRPMEMBER = ((SVC_ORG << 8) | 0x92);
    public static final int ORG_RSP_QUIT_GRPMEMBER = ((SVC_ORG << 8) | 0x93);
    public static final int ORG_REQ_SYN_ADD_GRP_USERS = ((SVC_ORG << 8) | 0x14);
    public static final int ORG_RSP_SYN_ADD_GRP_USERS = ((SVC_ORG << 8) | 0x94);
    public static final int ORG_REQ_SYN_DEL_GRP_USERS = ((SVC_ORG << 8) | 0x15);
    public static final int ORG_RSP_SYN_DEL_GRP_USERS = ((SVC_ORG << 8) | 0x95);
    public static final int ORG_REQ_SYN_DEL_GRP = ((SVC_ORG << 8) | 0x16);
    public static final int ORG_RSP_SYN_DEL_GRP = ((SVC_ORG << 8) | 0x96);
    public static final int ORG_REQ_SYN_MOD_GRP = ((SVC_ORG << 8) | 0x17);
    public static final int ORG_RSP_SYN_MOD_GRP = ((SVC_ORG << 8) | 0x97);
    public static final int ORG_REQ_TMPGRP_LIST = ((SVC_ORG << 8) | 0x18);
    public static final int ORG_RSP_TMPGRP_LIST = ((SVC_ORG << 8) | 0x98);
    public static final int ORG_REQ_TMPGRP_INFO = ((SVC_ORG << 8) | 0x19);
    public static final int ORG_RSP_TMPGRP_INFO = ((SVC_ORG << 8) | 0x99);
    public static final int ORG_REQ_TMPGRP_LIST_MEMBER = ((SVC_ORG << 8) | 0x1a);
    public static final int ORG_RSP_TMPGRP_LIST_MEMBER = ((SVC_ORG << 8) | 0x9a);
    public static final int ORG_REQ_ADD_TMPGRP = ((SVC_ORG << 8) | 0x1b);
    public static final int ORG_RSP_ADD_TMPGRP = ((SVC_ORG << 8) | 0x9b);
    public static final int ORG_REQ_MOD_TMPGRP = ((SVC_ORG << 8) | 0x1c);
    public static final int ORG_RSP_MOD_TMPGRP = ((SVC_ORG << 8) | 0x9c);
    public static final int ORG_REQ_DEL_TMPGRP = ((SVC_ORG << 8) | 0x1d);
    public static final int ORG_RSP_DEL_TMPGRP = ((SVC_ORG << 8) | 0x9d);
    public static final int ORG_REQ_ADD_TMPGRPMEMBER = ((SVC_ORG << 8) | 0x1e);
    public static final int ORG_RSP_ADD_TMPGRPMEMBER = ((SVC_ORG << 8) | 0x9e);
    public static final int ORG_REQ_DEL_TMPGRPMEMBER = ((SVC_ORG << 8) | 0x1f);
    public static final int ORG_RSP_DEL_TMPGRPMEMBER = ((SVC_ORG << 8) | 0x9f);
    public static final int ORG_REQ_QUIT_TMPGRPMEMBER = ((SVC_ORG << 8) | 0x20);
    public static final int ORG_RSP_QUIT_TMPGRPMEMBER = ((SVC_ORG << 8) | 0xa0);
    public static final int ORG_REQ_GRP_USER_LIST = ((SVC_ORG << 8) | 0x21);
    public static final int ORG_RSP_GRP_USER_LIST = ((SVC_ORG << 8) | 0xa1);
    public static final int ORG_REQ_SUB_DPT_STATU = ((SVC_ORG << 8) | 0x22);
    public static final int ORG_RSP_SUB_DPT_STATU = ((SVC_ORG << 8) | 0xa2);
    public static final int ORG_REQ_UNSUB_DPT_STATU = ((SVC_ORG << 8) | 0x23);
    public static final int ORG_RSP_UNSUB_DPT_STATU = ((SVC_ORG << 8) | 0xa3);
    public static final int ORG_REQ_PUB_DPT_STATU = ((SVC_ORG << 8) | 0x24);
    public static final int ORG_RSP_PUB_DPT_STATU = ((SVC_ORG << 8) | 0xa4);
    public static final int ORG_REQ_GRPID_LIST = ((SVC_ORG << 8) | 0x25);
    public static final int ORG_RSP_GRPID_LIST = ((SVC_ORG << 8) | 0xa5);

    public static final short P2P_REQ_SEND_MSG = ((SVC_P2P << 8) | 0x1);//∑¢ÀÕœ˚œ¢«Î«Û(PBReqSendMsg)
    public static final int P2P_RSP_SEND_MSG = ((SVC_P2P << 8) | 0x81);//∑¢ÀÕœ˚œ¢œÏ”¶(PBRspSendMsg)
    public static final short P2P_REQ_READ_MSG = ((SVC_P2P << 8) | 0x2);//∏¸–¬“—∂¡œ˚œ¢«Î«Û(PBReqReadIndex)
    public static final int P2P_RSP_READ_MSG = ((SVC_P2P << 8) | 0x82);//∏¸–¬“—∂¡œ˚œ¢œÏ”¶(none)
    public static final short P2P_REQ_HISTORY_MSG = ((SVC_P2P << 8) | 0x3);//ªÒ»°¿˙ ∑œ˚œ¢«Î«Û(PBReqHistoryMsg)
    public static final int P2P_RSP_HISTORY_MSG = ((SVC_P2P << 8) | 0x83);//ªÒ»°¿˙ ∑œ˚œ¢œÏ”¶(PBRspHistoryMsg)
    public static final int P2P_REQ_BCAST_MSG = ((SVC_P2P << 8) | 0x4);
    public static final int P2P_RSP_BCAST_MSG = ((SVC_P2P << 8) | 0x84);
    // 	P2P_REQ_RCNT_SSIONS         = ((SVC_P2P << 8) | 0x5 );//ªÒ»°◊ÓΩ¸ª·ª∞¡–±Ì«Î«Û(PBReqRcntSsions)
    // 	P2P_RSP_RCNT_SSIONS         = ((SVC_P2P << 8) | 0x85);//ªÒ»°◊ÓΩ¸ª·ª∞¡–±ÌœÏ”¶(PBRspRcntSsions)

    public static final short GRP_REQ_SEND_MSG = ((SVC_GRP << 8) | 0x1);//∑¢ÀÕœ˚œ¢«Î«Û(PBReqSendMsg)
    public static final int GRP_RSP_SEND_MSG = ((SVC_GRP << 8) | 0x81);//∑¢ÀÕœ˚œ¢œÏ”¶(PBRspSendMsg)
    public static final short GRP_REQ_READ_MSG = ((SVC_GRP << 8) | 0x2);//∏¸–¬“—∂¡œ˚œ¢«Î«Û(PBReqReadIndex)
    public static final int GRP_RSP_READ_MSG = ((SVC_GRP << 8) | 0x82);//∏¸–¬“—∂¡œ˚œ¢œÏ”¶(none)
    public static final short GRP_REQ_HISTORY_MSG = ((SVC_GRP << 8) | 0x3);//ªÒ»°¿˙ ∑œ˚œ¢«Î«Û(PBReqHistoryMsg)
    public static final int GRP_RSP_HISTORY_MSG = ((SVC_GRP << 8) | 0x83);//ªÒ»°¿˙ ∑œ˚œ¢œÏ”¶(PBRspHistoryMsg)
    public static final int GRP_REQ_SYN_ADD_GRP = ((SVC_GRP << 8) | 0x4);//‘ˆº”»∫»À‘±«Î«Û
    public static final int GRP_RSP_SYN_ADD_GRP = ((SVC_GRP << 8) | 0x84);//‘ˆº”»∫»À‘±œÏ”¶
    public static final int GRP_REQ_SYN_ADD_GRP_USERS = ((SVC_GRP << 8) | 0x5);//‘ˆº”»∫»À‘±«Î«Û
    public static final int GRP_RSP_SYN_ADD_GRP_USERS = ((SVC_GRP << 8) | 0x85);//‘ˆº”»∫»À‘±œÏ”¶
    public static final int GRP_REQ_SYN_DEL_GRP_USERS = ((SVC_GRP << 8) | 0x6);//…æ≥˝»∫»À‘±«Î«Û
    public static final int GRP_RSP_SYN_DEL_GRP_USERS = ((SVC_GRP << 8) | 0x86);//…æ≥˝»∫»À‘±œÏ”¶
    public static final int GRP_REQ_SYN_DEL_GRP = ((SVC_GRP << 8) | 0x7);//…æ≥˝»∫«Î«Û
    public static final int GRP_RSP_SYN_DEL_GRP = ((SVC_GRP << 8) | 0x87);//…æ≥˝»∫œÏ”¶

    public static final short RES_REQ_UPLOAD = ((SVC_RES << 8) | 0x1);//…œ¥´«Î«Û
    public static final short RES_RSP_UPLOAD = ((SVC_RES << 8) | 0x81);//…œ¥´œÏ”¶
    public static final short RES_REQ_DOWNLOAD = ((SVC_RES << 8) | 0x2);
    public static final int RES_RSP_DOWNLOAD = ((SVC_RES << 8) | 0x82);//œ¬‘ÿœÏ”¶
    public static final short RES_REQ_UPLOAD_FINISH = ((SVC_RES << 8) | 0x3);//…œ¥´ÕÍ≥…«Î«Û
    public static final short RES_RSP_UPLOAD_FINISH = ((SVC_RES << 8) | 0x83);//…œ¥´ÕÍ≥…œÏ”¶

    public static final int GTW_REQ_LOGIN = ((SVC_GTW << 8) | 0x1);//EWPƒ£ Ωµ«¬º«Î«Û(PBReqLogin)
    public static final int GTW_RSP_LOGIN = ((SVC_GTW << 8) | 0x81);//EWPƒ£ Ωµ«¬ºœÏ”¶(PBUserInfo)
    public static final int GTW_REQ_TOKEN = ((SVC_GTW << 8) | 0x2);//ªÒ»°EWP token«Î«Û(none)
    public static final int GTW_RSP_TOKEN = ((SVC_GTW << 8) | 0x82);//ªÒ»°EWP tokenœÏ”¶(token)
    // 	GTW_REQ_TODO_LIST           = ((SVC_GTW << 8) | 0x3 );//ªÒ»°¥˝∞Ï«Î«Û(none)
    // 	GTW_RSP_TODO_LIST           = ((SVC_GTW << 8) | 0x83);//ªÒ»°¥˝∞ÏœÏ”¶(none)
    // 	GTW_REQ_TOREAD_LIST         = ((SVC_GTW << 8) | 0x4 );//ªÒ»°¥˝‘ƒ«Î«Û(none)
    // 	GTW_RSP_TOREAD_LIST         = ((SVC_GTW << 8) | 0x84);//ªÒ»°¥˝‘ƒœÏ”¶(none)
    public static final int GTW_REQ_COMMON_APP = ((SVC_GTW << 8) | 0x5);//ªÒ»°≥£”√”¶”√«Î«Û(none)
    public static final int GTW_RSP_COMMON_APP = ((SVC_GTW << 8) | 0x85);//ªÒ»°≥£”√”¶”√œÏ”¶(PBRspToReadList)
    public static final int GTW_REQ_APP_LIST = ((SVC_GTW << 8) | 0x6);//ªÒ»°”¶”√«Î«Û(none)
    public static final int GTW_RSP_APP_LIST = ((SVC_GTW << 8) | 0x86);//ªÒ»°”¶”√œÏ”¶(PBRspToReadList)
    public static final int GTW_REQ_WORK_LIST = ((SVC_GTW << 8) | 0x7);//ªÒ»°¥˝∞Ï«Î«Û(none)
    public static final int GTW_RSP_WORK_LIST = ((SVC_GTW << 8) | 0x87);//ªÒ»°¥˝∞ÏœÏ”¶(none)
    public static final int SMS_REQ_SEND_MSG = ((SVC_SMS << 8) | 0x1);
    public static final int SMS_RSP_SEND_MSG = ((SVC_SMS << 8) | 0x81);//∑¢ÀÕº¥ ±∂Ã–≈
    public static final short P2P_REQ_HK_DEVICE_INFO = ((SVC_P2P << 8) | 0X9);//摄像头信息、
    public static final short P2P_RSP_HK_DEVICE_INFO = ((SVC_P2P << 8) | 0X89);//摄像头信息响应
    public static final short RES_REQ_VF_ADDR = ((SVC_RES << 8) | 0x4 );  //请求摄像头的ip和端口
    public static final short RES_RSP_VF_ADDR = ((SVC_RES << 8) | 0x84);  //接收摄像头的ip和端口

    public static final short VIDEO_REQ_RUNTIME_STREAM = ((SVC_VIDEO << 8) | 0x1);//请求实时流
    public static final short VIDEO_RSP_RUNTIME_STREAM = ((SVC_VIDEO << 8) | 0x81);//响应实时流
    public static final short VIDEO_RSP_PUSH_RUNTIME_STREAM = ((SVC_VIDEO << 8) | 0x83);
    public static final short VIDEO_REQ_STOP_PUSH_RUNTIME_STREAM	= ((SVC_VIDEO << 8) | 0x2);		//请求停止推送实时流
    public static final short VIDEO_RSP_STOP_PUSH_RUNTIME_STREAM	= ((SVC_VIDEO << 8) | 0x82);	//响应停止推送实时流

    public static final short VIDEO_REQ_CONTROL_DEVICE	 = ((SVC_VIDEO << 8) | 0x4); //请求控制设备
    public static final short VIDEO_RSP_CONTROL_DEVICE	 = ((SVC_VIDEO << 8) | 0x84);//响应控制设备


}
