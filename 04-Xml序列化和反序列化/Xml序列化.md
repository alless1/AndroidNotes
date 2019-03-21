# android中xml中序列化器用法 #

			//生成xml文件，利用XmlSerializer
			
			//1.获得序列化器
			XmlSerializer xmlSerializer = Xml.newSerializer();
			
			//应用程序默认可以写入数据目录是/data/data/包名/
			File file = new File("/data/data/com.itheima.xmlserializer/test.xml");
			
			FileOutputStream os = new FileOutputStream(file);
			
			//2.指定输出流和编码方式
			xmlSerializer.setOutput(os, "utf-8");
			
			//3.写文档的头	指定编码，是否独立xml文件
			xmlSerializer.startDocument("utf-8", true);
			
			//5.写开标签 map,无命名空间，标签名
			xmlSerializer.startTag(null, "map");
			
			//7.写开始标签 boolean
			xmlSerializer.startTag(null, "boolean");
			
			//9.写boolean标签中的属性value="true"
			xmlSerializer.attribute(null, "value", "true");
			
			//10.写boolean标签中的属性name="gps_config"
			xmlSerializer.attribute(null, "name", "gps_config");
			
			//8.写结束标签boolean
			xmlSerializer.endTag(null, "boolean");
			
			//6.写结束标签 map
			xmlSerializer.endTag(null, "map");
			//4.写文档结束
			xmlSerializer.endDocument();
			
			os.close();