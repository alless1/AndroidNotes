依赖：

	implementation 'javassist:javassist:3.12.1.GA'

测试代码：

    public static void updateAbObtainUtil(){
        try {
            ClassPool cPool = new ClassPool(true);
            //如果该文件引入了其它类，需要利用类似如下方式声明
            //cPool.importPackage("java.util.List");

            //设置class文件的位置
            cPool.insertClassPath("E:\\temp");

            //获取该class对象
            CtClass cClass = cPool.get("com.chuanglan.shanyan_sdk.utils.AbObtainUtil");

            //获取到对应的方法
            CtMethod cMethod = cClass.getDeclaredMethod("getSign");

            //更改该方法的内部实现
            //需要注意的是对于参数的引用要以$开始，不能直接输入参数名称
            cMethod.setBody("{ return \"50BFE2059A8FEA56398E7A5EBB6E68BF\"; }");

            //替换原有的文件
            cClass.writeFile("E:\\temp1");

            System.out.println("=======修改方法完=========");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

使用说明：

	https://lucumt.info/post/modify-java-class-file-content-directly/