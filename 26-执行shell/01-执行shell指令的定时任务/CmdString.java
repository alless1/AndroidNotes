package com.alless.rxsensedemo;

/**
 * Author:chengjie
 * Date:2018/7/6
 * Description:
 */
public class CmdString {
    public static final String INIT_CMD = "echo 1 > sys/class/amhdmitx/amhdmitx0/rxsense_policy";
    public static final String RXSENSE_STATE = "cat /sys/devices/virtual/switch/hdmi_rxsense/state";

}
