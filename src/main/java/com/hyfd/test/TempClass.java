package com.hyfd.test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class TempClass {
	
	private static String savePath = "g:\\新平台代理商折扣脚本\\";	// 生成文件存放的目录
	
	static String[] agentArray={
			"010cfe0819814b18be4e09a9957fb761",
			"01bd592059a94c89b7deecc08525434c",
			"0279479beaa74762b26a927186da3e23",
			"0329108a032a46fea87ff390f7bfea5a",
			"038f29158ea74051a956c116e4cc0012",
			"089659a2ceb547ffb0982c2e156290a9",
			"098672cbca9a48f4993c38f18006196b",
			"0a1b1fa35404472896624ec657235036",
			"106712a6083747528e2fdc6190d37a8b",
			"110d841f2aba4a64ad5522105c53d7ff",
			"13b4a883085d492fb463e22999622b2c",
			"14d1140a1a9a4f17967269cedf38678a",
			"15d4dff4d7fe43f9afb78b4c54709f54",
			"1633005983024804b07171803c6772f9",
			"1678312d02fb46cbbc9fd05ff64997e6",
			"16bb086bb15140bfb74e19c137778780",
			"170d543b85bd4261b25c417ad3fa0ead",
			"178085e1510d4843891a13e72d12ecb4",
			"1819f08b9c1e44518d3251af13f30ac5",
			"1871e98b5f0e4911ab8484214ba2d234",
			"19fff2d1df1d40fa987cfa07924ae159",
			"1d2c041d230f40bc9253ca195cca2248",
			"20fbb17682ee405c8dfaaf2e3c560896",
			"225eab9d3aab4df6998349436fb5395e",
			"22c865dbd78341f391659ff29f0279dc",
			"2363f1b750594f4c999d2d1175282c75",
			"23712a7b209f4be988b69afd044407c2",
			"26883f4fc4b54478a9161eb267da1e1e",
			"2a5076e5744848e8bf420b5294b89e3c",
			"2ac63d536ce84d189b57854c3f5c3146",
			"2b57564e2276450487469c6fa1e07d1a",
			"2c648fdec9304add8ff08237c83e8756",
			"2e0b7b91e087415db73dbab1fc466d8b",
			"302ec1545d6d4a328136b9db46ac4bdd",
			"32b84e28e6ad4158aac9b4a05da6e6fa",
			"33569634310640c39e669af00cd94021",
			"34d6af2c49ca4e409b3a1fbec3535d07",
			"3522311cb3a34350bfc676421c1b3fd0",
			"3b41d41ed0a94109a1b6ea82874c0859",
			"3e3685c9c90c4bf7bef47595eeed4043",
			"3f66afb4997f40898028a575c094e7d7",
			"41acbe08387c47e4bbe4fe46efa00da1",
			"41c773f17f7b4c0da94c06a39cd75f7a",
			"4430b149d6354eac8e2f095b04f2763a",
			"443b3ced94e64709ad0556d21e852a9e",
			"46976a56e7724b1d9b6f33b4338a832d",
			"496001640912486d9146f058331221ea",
			"49be4cb63e204409961087d36deccc7b",
			"4ac755a740064060a579767d32409550",
			"4c7d7c19894e47d1a4b9e6f220c690ea",
			"4c9574db431245b1b7450ae7ee73bd92",
			"4cb44d216e964cfdbb6724d32140d9c0",
			"4dfd54fb26e64603a6d3433471639733",
			"4ebe9ebf63ad42ab9ac9348b6d787d00",
			"51dd084698dd4337a94fe28286778fa4",
			"53e4b99cedde4a4facd581c98ad7c4bc",
			"54c7d237ab284aaaa9befbe85e053702",
			"561d26aa999f434ab37bf5a4845c6b7b",
			"58be4e24f7a947b5a1b992b351bf990a",
			"592c451bf3d44551a62b9f8a1f3ce767",
			"59f7b64d502b4bbb9a3fffc3a2cf0831",
			"5aee0eade07349baa45c35aa146e3ded",
			"5b01d52dbd9a45d3b27b2b38c9630dd6",
			"5b25ae7416f443749b5a415945a6eca5",
			"5bd1e23a3bbc40d08225cd3413762948",
			"5ca7654b24e94b53bad9dba40df14375",
			"5d6f6ad7da24429a88431ce4271e815a",
			"5ed0fdc8e6b8474588976aa1c540d974",
			"5f34028df1e94bcf9d5c54da16d4bf20",
			"600dae1e919647ff806617d25ac5046d",
			"6157a8fdd9b441ed8060651419852118",
			"6312cb3873cd41f092d53e3083199f97",
			"64ac508ca72b420cb8695d5c9a9664a3",
			"665911fd64614407830d14299e66d266",
			"66c9d0e9430d4e24bd9537a04386f441",
			"672985e7ff034e30b7f2fd57044b0ad7",
			"674f813dc56849598e0e5d1133e60125",
			"686f8e0cdf284f7e82e41acdfdada3c5",
			"6945ffe215bc49aa9ac47899a7e6461a",
			"69f18330a2584e1aa87df420f84461c4",
			"6ae9f657866e406aa3c9891a8b092c6d",
			"6b5bd26f2a7045cf990f34c472b3c6bb",
			"6bb235028a634edc8fefa7f2faa002e5",
			"6d03a1871cdf49939c5cbca492fa1913",
			"6ef414fd39dd458faa61f9c60d68bbf1",
			"6f51484f8df94f508613f70d703a835b",
			"6f6339c1a5ea45e6b3d2b68fdb25da1d",
			"6fed698119934ec19052002fb952a295",
			"7290f419337544baa0d53bb1ab351d99",
			"729ba00f06874e44a838a6f5766334c1",
			"73735c351c8d49ba8b9223c219152676",
			"7392eca9dabf442b853f2d59ef26edb9",
			"73eb609e713f4937bc0eb13d598ae803",
			"757ee86e750b46f2891f8590b1582199",
			"75d13cd624944be18dae27c3e1be6845",
			"7604142d2cb6477b89607502bb921025",
			"7639a543e5b3454ca6d8004a0f36b2fa",
			"76b9041d05844572922fc7d9c0d6d139",
			"7721e059a18b4f5ab022f7a747636db7",
			"786258ed2d3144668fc11b8a7ab71aec",
			"78a14e69cd8243b686f406d1b75214db",
			"7b3f782a8f0949cf98aaa3c5f3ce499f",
			"7c1070016475428ba5ba1022a6bcafb2",
			"7f230c81382b4ff0b06942c0b1645966",
			"7f9bae11926b449fb20f371084141956",
			"8003511c7ad54ffbaa3fd681f1ad0d87",
			"8108bacafcf041e296e85e25280240e3",
			"8126037482da464080bd6fa7df686e27",
			"825d90087b1f43a68f7f642167cce482",
			"82ae3075a2c64c15a4a00109c764b999",
			"8393ba6c18af494fac3749e413c365bf",
			"8552bccc02c64653ba28a2a1bb8125e1",
			"872d188980aa4551b9014d3a54c5d9c6",
			"88f711bf2019482eba4035d1459d795a",
			"89072ef3087b4d199f1d61114cbb198a",
			"89a36430c34b4a02a1a5b764a3592ff4",
			"8a86cc26f2c84726a80ab6ce4c06a4f2",
			"8b81359dfac849168a126d3e32a1ad59",
			"8c9b1d41bb314eb29053735cff31ed9d",
			"8ca8c7f5f88c4844a4056d770f2b2fd8",
			"8cf542675150461c8364c3c08ead0074",
			"942468614de846728b28848a3570d707",
			"942aef324f084583a9765cb9994e6c29",
			"94cb61902ecf4080ae3c4f23b28b098d",
			"983a26ae5ebb4736b777ffc1f1b35886",
			"98b804922b554379957f778b61acf735",
			"9f420f72bbe34fa8aa6cd2ee2232fb91",
			"a0159f5e84cc4e9f8d9aee1c67ddf7b8",
			"a1666d30d4ba42628c55b62fe9f731cf",
			"a2d33709c13b45e8be2b394ddf872848",
			"a401792982a548c681f8ce7ed65c8e5e",
			"a65d951bbcdf44c791bd4cbc257c554b",
			"a8e94fb4bc614f149cf4fbc84fc12fec",
			"a998891881d34d2880b845515b1f7801",
			"ac4417d0f61f4be6a7e95d94641de2b0",
			"ad17e60f717643c98c574bd968b1daa3",
			"aee279451f414674806aafcab40ef8de",
			"aee341e4f4644de288b9fdeb1b5269b2",
			"b0b8816d97be48b795dfa3956c6fff28",
			"b39e7e055b864204babb18b57ab635bb",
			"b4c0b405c11e4e658c2b86c13625f2ed",
			"b4eade6e3ac84f35be5cf10490bbe33b",
			"b50571c9ec2240a698e15b025fae8793",
			"b5c39a87d92b4e2180c47fb492b3fca2",
			"b66e2f519c464257aaf558504eeddc6e",
			"b714b38e671a45fe834dbdbed1e56d4d",
			"b9b674f88023455e88168fea8c8829a4",
			"bb381c7210444a87a5e1d62e84130822",
			"bb38c9e61ed9427cb0e5c67abf07d8e0",
			"bb63801b327f451c807899aae47cbb9c",
			"bbf3af83d05646119a7ab5baf56fabf0",
			"bc081703f46942f2ad02012055f43ac4",
			"bd7065bf40124341b346f3340b071de5",
			"bdea8ce7f7864e6594cec9f76f9ed8fb",
			"bfbf863c209b4e35b13555682c6b49fa",
			"c6760228f9e34583bc890140d08985a6",
			"ca431cd890564a47b15593cba5890aa0",
			"cb7cc38254b44069905a5f2ce92aa6b3",
			"ccd8eda0eebe4304afea57a0682e605b",
			"cd6eeb44ddb24eddafee0b1c8c389476",
			"ce0f254c20b34d42a2afacaad683a32d",
			"cfdfddc9a72c45d78fb9159499a249f8",
			"d07abd86db704e8c8c0f3bd1aee9f6c2",
			"d1013120caac42b68853e61850af3ab4",
			"d2e7f2a044a748e0b149da5f6cc40b8c",
			"d335f5ea4c9340169e8d2d04148d6f86",
			"d344f83c14ad4e43b8cd11532d532ff3",
			"d41753e3335b426ba1a5a0b5c2e9b959",
			"d554d0401bab4305bf85b78cc5266bb2",
			"d66be4ddf92443b7b7ec898c4814d9b8",
			"d69666d542704e72b9ac74d55f989e1e",
			"d7311d94614241a49c0b7066c44675ed",
			"d87c265056994b3889014588570c65b8",
			"d90fd48407a84e1d87ddb7433b2b9514",
			"d9aeaae7f1c4406894970224b7d3e147",
			"db04bc751e044a66972ec2404fc8d1ae",
			"db2ccab82b7248d88a4b71beb1f99aaa",
			"db7ba6b0daba468089943285d0159f09",
			"dbcb12c74ce44904b4168d8d442c785e",
			"ddb8145e63ab482a8b20c5fc5797a861",
			"de0cb2ec3f1d41cda8495aeaffa75a0c",
			"dea150f86dc54ed38bfaa885dbaabd77",
			"ded12e31aaf448d9b10d8dc31771e6b6",
			"df0cd739751144b7adec8144aa63cf7d",
			"dfba4dc87dcc4e08b8d1e061b8a10b01",
			"e06aee5452c544d99717fe8eac263680",
			"e132c11024f0407d8ec5e2fb0073bb3d",
			"e1d7bbcd1bb94a588b827ce2d03d317b",
			"e61569f2476f4715a12f224025b0f17d",
			"e63b508bdee84275bc9d4b97c2dd8bec",
			"e65fea3ff58f49b58af52ad1cbf2f5fb",
			"e670f2913a1b4792839d7e31dcadc90e",
			"e68f20ceed0744ab9f35cc8e68037898",
			"e76ed3a39bb1419eba7b5a0b9f4e7756",
			"e7d3e2ceb69244208e5e5cfb72661abd",
			"e906a6589a464d85a81696189c8b8681",
			"eb4fac452bc44db9a3f39bb4e5a734e0",
			"ec3b649e6ba94a699573948591722299",
			"f1b53da1c9e0433b917a3cffd2685bcb",
			"f36c842edc4b4d7786dd7b98e503a8e0",
			"f519d26b5cf44da0b107fb9e630a5849",
			"f5b39f50eabc451bbf1979a897152254",
			"f6b8e9a856454e75bd210beb780c981d",
			"f9423c12063542b8bb831e529e7061b2",
			"fa566ddf9f2e4fc498f3c57a48af60ac",
			"fd385fe2e54c4eefb6fa9465340f00f4",
			"fe804468d30543e599e3092e4873b45f",
			"ff58ec91e7b240f28a7e401dff33f0b7"
		};
	
	static String[] sqlArray={
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, '0695f27c743711e78aa56c92bf1513ac', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'b33c24927b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'b7fe97f47b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'bf67f59a7b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'c50d20207b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'caf526da7b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'cf0e50667b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);",
		"INSERT INTO `mp_agent_bill_discount` VALUES ('%s', NULL, '%s', '0000000030', '全国', NULL, 1.010000, 'd3380a6c7b4f11e7843d408d5c72fa31', NULL, NULL, '2018-2-27 14:48:56', 'ed76fe2722bb4e2497cb8ab8a4d48e7f', 1);"
	};
	
	static String delSql = "delete from mp_agent_bill_discount where id = '%s';";
	
	public static void creatSql(){
		String insertFileName = "代理商设置小米折扣sql";
		String deleteFileName = "删除代理商小米折扣sql";
		
		// 检查当前目录是否存在,如果不存在则创建该目录
		checkFolderExists(savePath);
		// 删除当前目录下所有文件
		removeFile4Dir(savePath);
		int agentArrayLength = agentArray.length;
		int sqlArrayength = sqlArray.length;
		int k = 1;
		for (int i = 0; i < agentArrayLength; i++) {
			for (int j = 0; j < sqlArrayength; j++) {
				String uuid = UUID.randomUUID().toString().replace("-", "");
				writeTxt(insertFileName,String.format(sqlArray[j], uuid ,agentArray[i]) +"-- " + k);
				writeTxt(deleteFileName, String.format(delSql, uuid) +"-- " + k++);
			}
		}
		System.out.println("执行完毕");
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
	 * <h5>功能:</h5>将生成的脚本写入到指定文件中
	 * 
	 * @author zhangpj	@date 2016年8月12日
	 * @param fileName
	 * @param str 
	 */
	public static void writeTxt(String fileName,String str){
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
			System.out.println("迪信通文件写入失败|"+savePath+fileName+"|"+str);
		}
	}
	
	/**
	 * 删除当前目录下所有文件
	 * 
	 * @updateDate 2014/07/22
	 * @param path	目录、路径
	 * @return 是否成功
	 */
	public static boolean removeFile4Dir(String path) {
		return removeFile4Dir(new File(path));
	}
	
	/**
	 * 删除当前文件下面所有文件
	 * 
	 * @updateDate 2014/07/22
	 * @param file	File 要删除的文件夹下面文件的文件对象
	 * @return 是否删除成功，如果有一个文件删除失败，将返回false
	 */
	private static boolean removeFile4Dir(File file) {
		boolean flag = false;
		if (file != null && file.exists() && file.isDirectory()) {
			File[] allFile = file.listFiles();
			for (File f : allFile) {
				flag = f.delete();
				if (!flag) {
					System.err.println("删除文件" + f.getAbsolutePath() + "出错了！");
					break;
				}
			}
		}
		return flag;
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 100; i++) {
//			System.out.println(UUID.randomUUID().toString().replace("-", ""));
//		}
		creatSql();
	}
}
