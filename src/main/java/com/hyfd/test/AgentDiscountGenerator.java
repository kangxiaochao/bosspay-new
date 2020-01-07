/**
 * 
 */
package com.hyfd.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.hyfd.common.DBUtils;
import com.hyfd.common.utils.DateUtils;

/**
 * <h5>描述:根据文件自动生成代理商折扣信息的sql</h5>
 * 
 * @author zhangpj	2018年9月15日 
 */
public class AgentDiscountGenerator {
	
//	private static int writeLoginnameCount;	// 生成文件的数量
	private static int writeCount;	// 生成文件的记录的条数
	private static String savePath = "g:\\代理商折扣信息设置\\";	// 生成文件存放的目录
	
	// 运营商名称
	private static String[] providerNameArray={"国美","远特","迪信通"};
	// 话费包大小
	private static String[] billPkgValueArray={"1","2","5"};
	// 代理商名称
	private static String[] agentNameArray={"ttbpczhuanyong","zhangxiaodong","baixinxin","yufei","jingxiang","w_dalutongxin","duzhongxin","w_fengxinghe","weihong","xushanghangyeka","w_zhongju","jiangningjun","huangyong","liujiang","houbaofan","w_yaobei","shaonan","w_runda","w_liudeliang","renjiming","duoduo","liujunli","19e","sangwenhua","heshuwei","apphoutai","yangjingshan","xubingxing","lineng","zhangjianwei","w_haoguangjie","lijunfeng","13583408052","qianguanbing","w_sunjianhui","lubao","wanghui","w_jinhaoshangmao","yangguoqiang","jibao","wangliming","zhaoyang","wubingbing","lishiquan","cuizhiyu","w_liuchengcai","macheng","zhaolinsong","wuhaipeng","chenqi","liuhaifeng","zhangxiaozhang","zhangmingli","yubin","wanggang","xiangshangjinfu","gaoxiang","wangshouhai","yanghaizhe","xiangjiaofei","liyongwei","fuhuabu","sunyanpeng","zhangzongming","mingzhongcheng","w_jiaoguangxi","shangbinglei","wangpanbin01","shandongdingxin","w_linyiwoyuan","xiesheng","lizhenhua","w_yangdongxia","wuxian","zhangxiaotong","lihuaiqin","qiangyanchun","lishengyong","sunshurong","zhangchanghe","yangzhisheng","chenjinrong","baimingqing","shizhengchang","liuchangzheng","liguixiang","zhangyanfang","huangxiaoen","w_wangzhiqiang","kezhijie","chenhui","liubin","zhangfacheng","w_huanghaitao","zhangjiawang","neibuhuafei","gongxiaoliang","zhangweikai","w_wanghongqiao","liusheng","w_lilei","wangcunzhu","sunminghui","w_guanxiaorong","longxu","yangjirui","zhaolijuan","xuhongwei","liuhua","w_zhanghaijun","w_yidingjiaofei","zhangyunming","yanyan","w_lifurong","tangtang","woniu","yueyong","hongfeng","hanweiwang","appzfb","newweixin","zhangjie","weixinying","zhangqiang","caiguobing","zhaokai","haojun","jiangxiangfeng","w_wangpanbin","zhanghaiqing","w_zhengzhoushoujika","liuqingfu","luochuan","w_zhangchunyang","yangshijie","lvdongfang","linzhongwei","w_wutianjiang","liuxiaoming","yangbin","yangjinhua","chenhongyun","lihongyue","w_yangguoqing","kongchunlei","renquan","gejiangbo","lijinsu","w_chenyongguo","liushude","zhangxinzhao","sundongdong","w_wanghai","hejingwen","kuangcuipeng","gmpczhuanyong","houyingzi","zuwei","zhengshunye","ceshi02","guxianghua","zhongyikeji","sangyingbo","wangsheng","tangxianzhang","zhangchangxing","chenyong","kehufuwu","gaoyunzhan","lishimin","test","qianmi","gushuai","w_songzijian","yinlaibao","yuante","yangjianmin","w_sunshibo","limengmeng","zhixinkeji","baiyingke","w_hongliangtongxun","yuxiangrong","yanpengzhuang","huwenpeng","zhangming","abeisha","jianghuaping","majunqiang","zhangyufeng","w_linweiji","zhuyafei","liuanmin","yangjianhua","w_liguodong","chenchen","w_yangjieling","zhaotieqiang1","gaoyangjiexun","geanning","w_yilin","xingbohai","weichengxinxi","zhengchuanling","qinxueliang","liudianming","weixinying01","yangyanqing","zhangminkan","sunjianjiang","wangfangfang","lixuejian","wangke","yizhenrong","limingxu","wuyonggang","machao","w_zhuchuanjun"};
	
	// 运营商集合
	private static List<Map<String, String>> providerList = getProviderInfo(providerNameArray); 
	// 话费包集合
	private static Map<Double, String> billPkgIdMap = getBillPkgInfo(billPkgValueArray);
	// 代理商集合
	private static List<String> agentIdList = getAgentInfo(agentNameArray);
	
	public static void main(String[] args) {
		checkFolderExists(savePath);
		
		// 生成客户折扣脚本及对应的删除sql脚本
		for (int i = 0; i < providerList.size(); i++) {
//			writeLoginnameCount = 1;
			writeCount = 0;
			for (Double key : billPkgIdMap.keySet()) {
				Double discout = 0d;
				if (key == 1) {
					discout = 1.02;
				} else if (key == 2){
					discout = 1.01;
				} else if (key == 5){
					discout = 1.004;
				}
				
				for (int j = 0; j < agentIdList.size(); j++) {
					createAgentDiscountSql(providerList.get(i).get("providerName"), providerList.get(i).get("providerId"), billPkgIdMap.get(key), agentIdList.get(j), discout);
				}
			}
			
			sleep(1);
			System.err.println("-------第"+(i+1)+"个文件["+providerList.get(i).get("providerName")+"],成功生成"+writeCount+"条记录-------\r");
			sleep(1);
		}
		System.out.println("所有文件生成完毕,请检查文件生成正确与否");
	}
	
	private static void createAgentDiscountSql(String fileName, String providerId, String billPkgId, String agentId, Double discout){
 		String id=UUID.randomUUID().toString().replace("-", "");
		String createDate = DateUtils.getNowTime();
		String suId = "ed76fe2722bb4e2497cb8ab8a4d48e7f";
		
		String sql = "INSERT INTO `mp_agent_bill_discount` (`id`, `agent_id`, `provider_id`, `province_code`, `discount`, `bill_pkg_id`, `create_date`, `create_user`, `del_flag`) "
				+ "VALUES ('%s', '%s', '%s', '全国', %f, '%s', '%s', '%s', 1) ; -- "+(++writeCount);
		sql = String.format(sql, id, agentId, providerId, discout, billPkgId, createDate, suId);
		writeTxt(fileName, sql);
		sql = "delete from mp_agent_bill_discount where id= '"+id+"'";
		writeTxt(fileName+"删除", sql);
		sleep(1);
	}
	
	/**
	 * <h5>功能:</h5>将生成的脚本写入到指定文件中
	 * 
	 * @author zhangpj	@date 2016年8月12日
	 * @param fileName
	 * @param str 
	 */
	private static void writeTxt(String fileName,String str){
		// 实现方法一
//		BufferedWriter writer;
//		try {
//			writer = new BufferedWriter(new FileWriter(new File(savePath+fileName+".txt"),true));
//			writer.write(str+"\r\n");
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// 实现方法二
		try {
			FileUtils.write(new File(savePath+fileName+".txt"), str+"\r\n",true);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("代理商折扣信息文件写入失败|"+savePath+fileName+"|"+str);
		}
	}
	
	/**
	 * <h5>功能:</h5>检查当前目录是否存在,如果不存在则创建该目录(可创建多层目录)
	 * 
	 * @author zhangpj	@date 2016年8月12日
	 * @param path 文件路径
	 */
	private static void checkFolderExists(String path){
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * <h5>功能:</h5>休眠
	 * 
	 * @author zhangpj	@date 2016年8月12日
	 * @param millisecond 休眠时长(毫秒)
	 */
	private static void sleep(int millisecond){
		try {
			Thread.currentThread().sleep(millisecond);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @功能描述：	拼接成 sql  in 需要的条件 ('a','b','c'......)
	 *
	 * @作者：zhangpj		@创建时间：2015-3-31
	 * @param String[] ids
	 * @return	('a','b','c'......)
	 *
	 * @修改描述：	
	 *
	 * @修改者：			@修改时间：
	 */
	private static String getStr(String[] ids){
		String str="";
		if(null!=ids&&ids.length>0){
			str+="'";
			for(int i=0;i<ids.length-1;i++){
				str+=ids[i]+"','";
			}
			str+=ids[ids.length-1]+"'";
		}
		return str;
	}
	
	/**
	 * <h5>功能:获取运营商信息</h5>
	 * 
	 * @author zhangpj	@date 2018年9月15日
	 * @param providerNameArray
	 * @return 
	 */
	private static List<Map<String, String>> getProviderInfo(String[] providerNameArray){
		String sql = "select id,name from mp_provider where `name` in ("+getStr(providerNameArray)+")";
		List<Map<String, String>> providerIdList = new ArrayList<Map<String, String>>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			Map<String, String> map = null;
			while(rs.next()){
				map = new HashMap<String, String>();
				map.put("providerId", rs.getString("id"));
				map.put("providerName", rs.getString("name"));
				providerIdList.add(map);
			}
		} catch (Exception e) {
			System.err.println("获取运营商信息发生异常");
		} finally {
			DBUtils.closeResources(conn, pstmt, rs);
		}
		return providerIdList;
	}
	
	/**
	 * <h5>功能:获取话费包信息</h5>
	 * 
	 * @author zhangpj	@date 2018年9月15日
	 * @param billPkgValue
	 * @return 
	 */
	private static Map<Double, String> getBillPkgInfo(String[] billPkgValueArray){
		String sql = "select * from mp_bill_pkg where value in("+getStr(billPkgValueArray)+")";
		Map<Double, String> map = new HashMap<Double, String>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();

			while(rs.next()){
				map.put(rs.getDouble("price"), rs.getString("id"));
			}
		} catch (Exception e) {
			System.err.println("获取话费包信息发生异常" + e.getMessage());
		} finally {
			DBUtils.closeResources(conn, pstmt, rs);
		}
		return map;
	}
	
	/**
	 * <h5>功能:获取代理商信息</h5>
	 * 
	 * @author zhangpj	@date 2018年9月15日
	 * @param agentNameArray
	 * @return 
	 */
	private static List<String> getAgentInfo(String[] agentNameArray){
		String sql = "select id from mp_agent where name in("+getStr(agentNameArray)+")";
		List<String> agentIdList = new ArrayList<String>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtils.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				agentIdList.add(rs.getString(1));
			}
		} catch (Exception e) {
			System.err.println("获取代理商信息发生异常" + e.getMessage());
		} finally {
			DBUtils.closeResources(conn, pstmt, rs);
		}
		return agentIdList;
	}
	
}
