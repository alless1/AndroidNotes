### 宝塔centos7.x防火墙设置

1. 下载iptables的一些服务

   ~~~java
   yum -y install iptables
   yum -y install iptables-services
   yum -y install ipset
   ~~~

2. 关闭firewall防火墙（使用iptables）

   ~~~java
   systemctl stop firewalld.service
   systemctl disable firewalld.service
     
   默认防火墙状态
   firewall-cmd --state
   ~~~

3. 清空规则

   ~~~java
   #防止设置不生效，建议清空下之前的防火墙规则
   iptables -P INPUT ACCEPT//默认允许所有
   iptables -F//清除规则
   ~~~

4. 创建新的规则到集合（获取国内ip段）

   ~~~java
   #创建一个名为cnip的规则
   ipset -N cnip hash:net
   #下载国家IP段，这里以中国为例
   wget -P . http://www.ipdeny.com/ipblocks/data/countries/cn.zone
   #将IP段添加到cnip规则中
   for i in $(cat /root/cn.zone ); do ipset -A cnip $i; done
   ~~~

5. 查看集合

   ~~~java
   ipset list cnip
   ~~~

6. 设置到iptables规则里（添加白名单）

   ~~~java
   #放行IP段
   iptables -A INPUT -p tcp -m set --match-set cnip src -j ACCEPT
   
   //自定义白名单
   iptables -A INPUT -p tcp -m set --match-set myip src -j ACCEPT
   //中国移动
   iptables -A INPUT -p tcp -m set --match-set cmcc src -j ACCEPT
   
   
   #默认拦截
   iptables -P INPUT DROP
   ~~~

7. 宝塔终端可能会出现登录不上的情况，放行127.0.0.1

   ~~~java
   #放行127.0.0.1
   iptables -A INPUT -s 127.0.0.1 -j ACCEPT
   ~~~

8. 保存规则和重启

   ~~~java
   service iptables save
   service iptables restart
     
   iptables-save
   systemctl restart iptables
   ~~~

9. 查看规则

   ~~~java
   iptables -L
   ~~~

   

~~~java
添加条目
  ipset -A cnip xxxx
删除条目
  ipset -D cnip xxxx
清空集合
  ipset flush cnip
查询某个ip
  ipset test cnip x.x.x.x

iptabels 列出规则
iptables -L --line-numbers
iptables 删除指定条目规则
iptables -D INPUT 3
  
  
上面的ip不全，其他ip地址下载
https://www.ip2location.com/free/visitor-blocker
~~~

