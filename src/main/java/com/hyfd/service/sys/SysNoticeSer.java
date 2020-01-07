package com.hyfd.service.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.sys.SysNoticeDao;
import com.hyfd.service.BaseService;

import io.goeasy.GoEasy;
import io.goeasy.publish.GoEasyError;
import io.goeasy.publish.PublishListener;

@Service
@Transactional
public class SysNoticeSer extends BaseService
{
public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private SysNoticeDao noticeDao;
    
    /**
     * @功能描述：	分页获取公告信息
     *
     * @作者：zhangpj		@创建时间：2017年8月7日
     * @param req
     * @return
     */
    public String sysNoticeList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			Page p=getPage(m);//提取分页参数
			int total= noticeDao.getNoticeCount(m);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList=noticeDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
    
    /**
     * @功能描述：	新增一条新公告
     *
     * @作者：zhangpj		@创建时间：2017年8月8日
     * @param req
     * @return
     */
    public String sysNoticeAdd(HttpServletRequest req){
		boolean flag=false;
		try{
			// 获取参数
			Map<String, Object> myData=getMaps(req);
			if(myData.get("order").toString().equals("2")) {
				GoEasy goEasy = new GoEasy("rest-hangzhou.goeasy.io","BC-52d6cde604c340d5a3aa951e6769b605");
				goEasy.publish("系统提示",myData.get("content").toString(), new PublishListener(){
					@Override
					public void onSuccess() {
					    System.out.print("消息发布成功。");
					    log.debug("消息发布成功");
					}
					@Override
					public void onFailed(GoEasyError error) {
						System.out.print("消息发布失败,错误编码：" + error.getCode() + "错误信息：" +error.getContent());
						log.debug("消息发布失败,错误编码：" + error.getCode() + "错误信息：" +error.getContent());
					}
				});
			}
			// 获取当前用户信息
			Map<String, Object> userInfoMap = getUser();
            myData.put("createUser", userInfoMap.get("suId"));
            
			int rows=noticeDao.insert(myData);
			if(rows>0){
				flag=true;
			}
			Session session=getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/sysNoticeListPage";
	}
    
    /**
     * @功能描述：	跳转到公告编辑页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param id
     * @return
     */
    public String sysNoticeEditPage(String id){
		try{
			Map<String, Object> sysNotice= noticeDao.selectByPrimaryKey(id);
			Session session=getSession();
			session.setAttribute("sysNotice", sysNotice);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "system/sysNoticeEdit";
	}
    
    /**
     * @功能描述：保存公告编辑后的信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    public String sysNoticeEdit(HttpServletRequest req)
    {
    	boolean flag = false;
        try
        {
            Map<String, Object> myData = getMaps(req);
            
            // 获取当前用户信息
 			Map<String, Object> userInfoMap = getUser();
            myData.put("updateUser", userInfoMap.get("suId"));
            
            int rows = noticeDao.updateByPrimaryKey(myData);
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
        
        return "sysNoticeListPage";
    }
    
    /**
     * @功能描述：	假删,更新删除标志为已删除
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param id
     * @return
     */
    public String sysNoticeDel(String id){
    	boolean flag = false;
        try
        {
            Map<String, Object> myData = new HashMap<String, Object>();
            myData.put("id", id);
            myData.put("delFlag", 0);
            
            // 获取当前用户信息
 			Map<String, Object> userInfoMap = getUser();
            myData.put("updateUser", userInfoMap.get("suId"));
            
            int rows = noticeDao.updateByPrimaryKeySelective(myData);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
    	
    	return "sysNoticeListPage";
    }
    
    /**
     * @功能描述： 查询平台公告
     * @param req
     * @return
     */
    public String sysNoticeList1(HttpServletRequest req) {
    	Map<String, Object> m=getMaps(req); //封装前台参数为map
		List<Map<String, Object>> dataList=noticeDao.selectList(m);
		String dataListJson=BaseJson.listToJson(dataList);
    	return dataListJson;
    }
}
