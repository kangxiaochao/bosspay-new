package com.hyfd.service.sys;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GenerateData;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.common.ToolPbkdf2;
import com.hyfd.common.utils.CaptchaUtil;
import com.hyfd.dao.sys.SysUserDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class SysUserSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired(required = true)
    private SysUserDao sysUserDao;
    
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    
    /**
     * 登录业务逻辑
     * 
     * @param req
     * @return
     */
    public String login(HttpServletRequest req)
    {
        try
        {
            Map<String, Object> m = getMaps(req);
            Session session = getSession();
            String suName = (String)m.get("suName");
            String suPass = (String)m.get("suPass");
            // String captcha = (String)m.get("captcha");
            // String randomString = (String) session.getAttribute("randomString");
            // if(captcha.equalsIgnoreCase(randomString)){
            if (StringUtils.isNotEmpty(suName) && StringUtils.isNotEmpty(suPass))
            {
                // suPass=DigestUtils.md5Hex(suPass.getBytes());
                // 使用shiro管理登录
                SecurityUtils.getSubject().login(new UsernamePasswordToken(suName, suPass));
                Map<String, Object> userInfoMap = getUser();
                String suId = userInfoMap.get("suId") + "";
                session.setAttribute("suName", suName);
                session.setAttribute("suId", suId);
                if("e9ce439577e94e92a2745f0ec40c404f".equals(suId)) {
                	 SecurityUtils.getSubject().getSession().setTimeout(4320000);
                }
                return "redirect:/mainPage";
            }
            else
            {
                req.setAttribute("backMsg", "用户名或密码为空！");
                return "login";
            }
            // }else{
            // req.setAttribute("backMsg", "验证码输入错误！");
            // return "login";
            // }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
            req.setAttribute("backMsg", "用户名或密码错误请重新输入！");
            return "login";
        }
    }
    
    /**
     * 获取登录页面验证码
     * */
    public void captcha(HttpServletRequest req, HttpServletResponse res)
    {
        try
        {
            CaptchaUtil.outputCaptcha(req, res);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
    }
    
    /**
     * 根据用户名获取记录
     * 
     * @param suName
     * @return
     */
    public Map<String, Object> getSysUserBySuName(String suName)
    {
        Map<String, Object> m = null;
        try
        {
            m = sysUserDao.getSysUserBySuName(suName);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    /**
     * 根据主键获取记录
     * 
     * @param suId
     * @return
     */
    public Map<String, Object> getSysUserBySuId(String suId)
    {
        Map<String, Object> m = null;
        try
        {
            m = sysUserDao.getSysUserBySuId(suId);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    /**
     * 获取用户数量
     * 
     * @param m
     * @return
     */
    public int getSysUserCount(Map<String, Object> m)
    {
        int sysUserCount = 0;
        try
        {
            sysUserCount = sysUserDao.getSysUserCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sysUserCount;
    }
    
    public int getSysUserCount(HttpServletRequest req)
    {
        Map<String, Object> m = getMaps(req); // 封装前台参数为map
        int sysUserCount = 0;
        try
        {
            sysUserCount = sysUserDao.getSysUserCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sysUserCount;
    }
    
	/**
	 * @功能描述：	验证当前用户名是否已存在
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月17日
	 */
	public int sysUserCountBySuName(HttpServletRequest req) {
		Map<String, Object> m = getMaps(req); // 封装前台参数为map
		int sysUserCount = 0;
		try {
			Map<String, Object> sysUser = sysUserDao.getSysUserBySuName(m.get("suName").toString());
			if (null != sysUser) {
				sysUserCount = 1;
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return sysUserCount;
	}
    
    /**
     * 获取用户列表数据并生成json
     * 
     * @param req
     * @return
     */
    public String sysUserList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getSysUserCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> dataList = sysUserDao.getSysUserList(m);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    /**
     * 添加用户信息
     * 
     * @param req
     * @return
     */
    public String sysUserAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myData = getMaps(req);
            
            int rows = saveUser(myData);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/sysUserListPage";
    }
    
    /**
     * @功能描述： 保存用户信息到数据库
     *
     * @作者：zhangpj @创建时间：2017年4月20日
     * @param myData
     * @return
     * @throws Exception
     */
    public int saveUser(Map<String, Object> myData)
        throws Exception
    {
        // 生成用户id
        String suId = myData.get("suId") + "";
        if (null == suId || "null".equals(suId) || "".equals(suId))
        {
            suId = GenerateData.getUUID();
        }
        
        String suPass = (String)myData.get("suPass");
        byte[] salt = ToolPbkdf2.generateSalt();
        
        byte[] encryptedPassword = ToolPbkdf2.getEncryptedPassword(suPass, salt);
        
        myData.put("suId", suId);
        myData.put("salt", salt);
        myData.put("password", encryptedPassword);
        
        int rows = sysUserDao.sysUserAdd(myData);
        return rows;
    }
    
    /**
     * 获取用户详细信息
     * 
     * @param suId
     */
    public String sysUserDetail(String suId)
    {
        try
        {
            Map<String, Object> m = sysUserDao.getSysUserBySuId(suId);
            Session session = getSession();
            session.setAttribute("sysUser", m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/sysUserDetailPage";
    }
    
    /**
     * 修改用户信息
     * 
     * @param req
     * @param suId
     * @return
     */
    public String sysUserEdit(HttpServletRequest req, String suId)
    {
        
        try
        {
            boolean flag = false;
            Map<String, Object> myData = getMaps(req);
            
            int rows = sysUserDao.sysUserEdit(myData);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "sysUserListPage";
    }
    
    /**
     * 用户密码修改
     * 
     * @param req
     * @param suId
     * @return
     */
    public String updateUserPass(HttpServletRequest req, String suId)
    {
        try
        {
            boolean flag = false;
            Map<String, Object> myData = getMaps(req);
            
            String suPass = (String)myData.get("suPass");
            byte[] salt = ToolPbkdf2.generateSalt();
            
            byte[] encryptedPassword = ToolPbkdf2.getEncryptedPassword(suPass, salt);
            
            myData.put("salt", salt);
            myData.put("password", encryptedPassword);
            
            int rows = sysUserDao.sysUserPassChange(myData);
            if (rows > 0)
            {
                flag = true;
            }
            
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "密码修改成功" : "密码修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mainPage";
    }
    
    /**
     * 用户密码修改
     * 
     * @param req
     * @param suId
     * @return
     */
    public String sysUserChangePass(HttpServletRequest req, String suId)
    {
        try
        {
            boolean flag = false;
            Map<String, Object> myData = getMaps(req);
            
            String suPass = (String)myData.get("suPass");
            byte[] salt = ToolPbkdf2.generateSalt();
            
            byte[] encryptedPassword = ToolPbkdf2.getEncryptedPassword(suPass, salt);
            
            myData.put("salt", salt);
            myData.put("password", encryptedPassword);
            
            int rows = sysUserDao.sysUserPassChange(myData);
            if (rows > 0)
            {
                flag = true;
            }
            
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "密码修改成功" : "密码修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "sysUserListPage";
    }
    
    /**
     * 跳转到编辑用户页
     * 
     * @param suId
     * @return
     */
    public String sysUserEditPage(String suId)
    {
        try
        {
            Map<String, Object> sysUser = getSysUserBySuId(suId);
            Session session = getSession();
            session.setAttribute("sysUser", sysUser);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "system/sysUserEdit";
    }
    
    /**
     * 跳转到密码修改页
     * 
     * @param suId
     * @return
     */
    public String sysUserChangePassPage(String suId)
    {
        try
        {
            Map<String, Object> sysUser = getSysUserBySuId(suId);
            Session session = getSession();
            session.setAttribute("sysUser", sysUser);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "system/sysUserChangePass";
    }
    
    /**
     * 跳转到密码修改页
     * 
     * @param suId
     * @return
     */
    public String userChangePassPage(String suId)
    {
        try
        {
            Map<String, Object> sysUser = getSysUserBySuId(suId);
            Session session = getSession();
            session.setAttribute("sysUser", sysUser);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "system/userChangePass";
    }
    
    /**
     * 删除用户信息
     * 
     * @param suId
     * @return
     */
    public String sysUserDel(String suId)
    {
        
        Session session = getSession();
        try
        {
            boolean flag = false;
            int rows = sysUserDao.sysUserDel(suId);
            if (rows > 0)
            {
                flag = true;
            }
            
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
            
        }
        catch (Exception e)
        {
            getMyLog(e, log);
            session.setAttribute(GlobalSetHyfd.backMsg, "删除失败,请检查其他模块是否关联了此用户!");
        }
        return "sysUserListPage";
    }
    
    /**
     * 注销方法
     * 
     * @param req
     * @return
     */
    public String logout()
    {
        SecurityUtils.getSubject().logout();
        Session session = getSession();
        session.setAttribute(GlobalSetHyfd.backMsg, "您已经安全退出登陆！");
        return "login";
    }
    
    /**
     * @功能描述： 任缴费判断用户是否存在
     *
     * @作者：zhangjun @创建时间：2018年01月25日
     * @param myData
     * @return
     * @throws Exception
     */
    public String appLogin(HttpServletRequest req){
    	Map<String, Object> m = getMaps(req);
    	String suName = (String)m.get("userName");
    	String suPass = (String)m.get("passWord");
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> resultMap = null;
    	resultMap = sysUserDao.getSysUserBySuName(suName);
        if(null == resultMap){
        	result.put("state",0);
        	result.put("msg","用户不存在");
        }else {
        	//用户密码验证
        	byte[] salt = (byte[])resultMap.get("salt");// 密码盐
     		byte[] encryptedPassword = (byte[])resultMap.get("password");
     		boolean bool = false;
     		try {
     			bool = ToolPbkdf2.authenticate(suPass, encryptedPassword, salt);
     		} catch (NoSuchAlgorithmException e) {
     			e.printStackTrace();
     		} catch (InvalidKeySpecException e) {
     			e.printStackTrace();
     		}
            if(bool){
            	result.put("state",1);
            	result.put("msg","用户验证成功");
            }else {
            	result.put("state",0);
            	result.put("msg","用户密码错误");
			}
        }
        String resultStr = BaseJson.mapToJson(result);
        return resultStr;
    }
    
}
