package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.RabbitmqPhysicalChannelSer;

@Controller
public class RabbitmqPhysicalChannelCtl extends BaseController {

	@Autowired
	private RabbitmqPhysicalChannelSer rabbitmqPhysicalChannelSer;

	/**
	 * @功能描述： 跳转到物理通道对应的MQ通道列表页面
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @return
	 */
	@GetMapping("rabbitmqPhysicalChannelListPage")
	public String rabbitmqPhysicalChannelListPage() {
		return "mp/rabbitmqPhysicalChannelList";
	}

	/**
	 * @功能描述： 获取物理通道对应的MQ通道列表信息
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @param req
	 * @return
	 */
	@GetMapping("rabbitmqPhysicalChannel")
	@ResponseBody
	public String rabbitmqPhysicalChannelGet(HttpServletRequest req) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelList(req);
	}
	
	/**
	 * @功能描述：	获取所有物理通道对应的MQ通道列表信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月5日
	 */
	@GetMapping("rabbitmqPhysicalChannelAll")
	@ResponseBody
	public String rabbitmqPhysicalChannelAlGet(HttpServletRequest req) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelListAll(req);
	}

	/**
	 * @功能描述： 跳转到物理通道对应的MQ通道添加页面
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @return
	 */
	@GetMapping("rabbitmqPhysicalChannelAddPage")
	public String rabbitmqPhysicalChannelAddPage() {
		return "mp/rabbitmqPhysicalChannelAdd";
	}

	/**
	 * @功能描述： 添加物理通道对应的MQ通道信息
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @param req
	 * @return
	 */
	@PostMapping("rabbitmqPhysicalChannel")
	public String rabbitmqPhysicalChannelPost(HttpServletRequest req) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelAdd(req);
	}

	/**
	 * @功能描述： 跳转到物理通道对应的MQ通道编辑页面
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @return
	 */
	@GetMapping("rabbitmqPhysicalChannelEditPage/{id}")
	public String rabbitmqPhysicalChannelEditPage(@PathVariable("id") String id) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelEditPage(id);
	}

	/**
	 * @功能描述： 更新物理通道对应的MQ通道列表信息
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @param req
	 * @return
	 */
	@PutMapping("rabbitmqPhysicalChannel")
	@ResponseBody
	public String rabbitmqPhysicalChannelPut(HttpServletRequest req) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelEdit(req);
	}

	/**
	 * @功能描述： 删除物理通道对应的MQ通道列表指定信息
	 *
	 * @作者：zhangpj @创建时间：2018年1月29日
	 * @param req
	 * @return
	 */
	@DeleteMapping("rabbitmqPhysicalChannel/{id}")
	@ResponseBody
	public String rabbitmqPhysicalChannelDelete(@PathVariable("id") String id) {
		return rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelDel(id);
	}
}
