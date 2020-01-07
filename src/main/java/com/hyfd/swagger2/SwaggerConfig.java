package com.hyfd.swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.*;

@EnableSwagger2
public class SwaggerConfig {

	/** =============================BaseUser start============================= **/

	@Bean
	public Docket ApiByBaseUser() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("ApiByBaseUser")
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false).forCodeGeneration(false)
				.pathMapping("/").select()
				.paths(regex("/*.*")).build()
				.apiInfo(ApiInfoByBaseUser());
	}

	private ApiInfo ApiInfoByBaseUser() {
		return new ApiInfoBuilder()
				.title("用户模块测试a")
				.description("this can write some description")
				.termsOfServiceUrl("http://www.baidu.com")
				.contact(
						new Contact("vsked", "http://www.haoyafeida.com/",
								"2521744172@qq.com")).version("0.0.1").build();
	}

	/** =============================BaseUser end============================= **/

	/** =============================S1 start============================= **/

	@Bean
	public Docket ApiByS1() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("ApiByS1")
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false).forCodeGeneration(false)
				.pathMapping("/").select()
				.paths(regex("/*.*")).build()
				.apiInfo(ApiInfoByS1());
	}

	private ApiInfo ApiInfoByS1() {
		return new ApiInfoBuilder()
		.title("用户模块测试b")
		.description("this can write some description")
		.termsOfServiceUrl("http://www.baidu.com")
		.contact(
				new Contact("vsked", "http://www.haoyafeida.com/",
						"2521744172@qq.com")).version("0.0.1").build();
	}
	/** =============================xulei end============================= **/
}