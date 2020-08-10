
http://localhost:8080/bosspay

springframework+springmvc+shiro+mybatis+分页+log4j1+swagger+maven

GET /session # 获取会话信息 
POST /session # 创建新的会话（登入） 
PUT /session # 更新会话信息 
DELETE /session # 销毁当前会话（登出） 

sysUserListPage
跳转到用户列表页 get

sysUser
获取所有用户数据 get

sysUserAddPage
用户添加页面显示 只能使用get方式提交


sysUserDetailPage
跳转到详情页面 要使用get方式提交

sysUserEditPage/{suId}
显示编辑页面get请求路径中要包括需要修改的ID 

sysUserChangePassPage/{suId}
跳转到用户密码修改页get请求路径中要包括需要修改的ID 

sysUser/{suId}
显示详单页面要使用get方法并要在请求路径中传入用户编号数据

sysUser
添加用户 POST

sysUser/{suId}
更新用户信息 只能用put方式提交

sysUser/{suId}
更新用户部部分属性 patch 在服务器更新资源（客户端提供改变的属性）

sysUser/{suId}
删除用户数据  只能用delete请求需要在请求路径中添加要删除的用户编号


GET（SELECT）：从服务器取出资源（一项或多项）。
POST（CREATE）：在服务器新建一个资源。
PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
DELETE（DELETE）：从服务器删除资源。
HEAD：获取资源的元数据。
OPTIONS：获取信息，关于资源的哪些属性是客户端可以改变的。


admin
000000

zongjingli
fuzongjingli
ciwuzongjian
xushangzongjingli
dajiangyouluguo

http://localhost:8080/

http://localhost:8080/birdeggproto1/swagger-ui.html


rest
http://blog.csdn.net/w605283073/article/details/51338765

查德森模型
Rest in Practice

HATEOAS

ETag
http://jinnianshilongnian.iteye.com/blog/1508016
http://blog.csdn.net/zhongweijian/article/details/9006853



特别注意
tomcat8开启delete支持在conf目录中web.xml目录中修改
org.apache.catalina.servlets.DefaultServlet
添加参数

        <init-param>     
            <param-name>readonly</param-name>    
            <param-value>false</param-value>  
        </init-param>
     
