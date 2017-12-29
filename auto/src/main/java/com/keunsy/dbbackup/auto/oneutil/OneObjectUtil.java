package com.keunsy.dbbackup.auto.oneutil;

import com.keunsy.dbbackup.core.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


/**
 * 用于处理只有一个启动项的程序
 * 
 * 
 */
public class OneObjectUtil {

    private static String methodIndex = "exe";

    /**
     * 加载socket配置文件
     */
    private static Properties loadProperties() {

        Properties prop = new Properties();
        InputStream is = null;
        try {
            try {
                String path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
                is = new FileInputStream(path + "config/oneObjectUtil.properties");
            } catch (Exception e) {
                System.err.println("加载自定义配置文件异常config/oneObjectUtil.properties");
            }
            if (is == null) {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream("oneObjectUtil.properties");
            }
            if (is == null) {
                // 加载类目录下的资源文件
                is = OneObjectUtil.class.getResourceAsStream("oneObjectUtil.properties");
            }
            prop.load(is);

        } catch (Exception e) {
            LogUtil.ERROR_LOG.error("", e);
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return prop;
    }

    /**
     * 启动某个端口的监听程序防止启动重复
     */
    public static void listen(IOneObjectInvokable object) {

        Properties prop = loadProperties();
        ServerSocket ss = null;
        try {

            System.out.println("OneObjectUtil try to listen " + prop.getProperty("h") + ":" + prop.getProperty("p"));
            // 绑定 socket
            ss = new ServerSocket();
            ss.bind(new InetSocketAddress(prop.getProperty("h"), Integer.parseInt(prop.getProperty("p"))));
            System.out.println("OneObjectUtil listen " + prop.getProperty("h") + ":" + prop.getProperty("p")
                    + " start success!!");
            System.out.println("OneObjectUtil try invoke object start method! ");
            // 启动线程
            object.start();
            System.out.println("OneObjectUtil try invoke object start method success");
            // 根据用户命名 执行相应操作
            Socket st = null;
            while (true) {
                try {
                    st = ss.accept();
                    commandClass command = explainCommandLint(readInputStream(st.getInputStream()));
                    if ("exit".equalsIgnoreCase(command.getMethodName())) {
                        break;
                    } else if ("stop".equalsIgnoreCase(command.getMethodName())) {
                        System.out.println("OneObjectUtil try invoke object stop method! ");
                        object.stop();
                        System.out.println("OneObjectUtil try invoke object stop method success");
                    } else {
                        exeCommand(command, st, object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        st.close();
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.ERROR_LOG.error("", e);
            e.printStackTrace();
        } finally {
            try {
                object.stop();
            } catch (Exception e) {
                LogUtil.ERROR_LOG.error("", e);
            }

            try {
                ss.close();
            } catch (Exception e) {
                LogUtil.ERROR_LOG.error("", e);
            }
            System.out.println("OneObjectUtil listen " + prop.getProperty("h") + ":" + prop.getProperty("p")
                    + " stop success!!");
        }
    }

    private static String readInputStream(InputStream is) throws Exception {
        // 接收命令和参数
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1];
        int read = 0;
        while ((read = is.read(b)) != -1) {
            if (b[0] == 0x00) {
                break;
            }
            bos.write(b, 0, read);

        }
        return new String(bos.toByteArray());
    }

    /**
     * 
     * 
     * help(接收到help 命令时)
     * 
     * @param name
     * @param @return 设定文件
     * @return String DOM对象
     * @Exception 异常对象
     * @since CodingExample　Ver(编码范例查看) 1.1
     */
    private static String help(Object o) {

        String newSeparator = System.getProperty("line.separator");
        StringBuffer helpContentBuffer = new StringBuffer("List of commands:").append(newSeparator);
        helpContentBuffer.append("command like 'methodName -argName1 argValue1 -argName2 argValue2'").append(
                newSeparator);
        helpContentBuffer.append("h").append("\t").append("connect host").append(newSeparator);
        helpContentBuffer.append("p").append("\t").append("connect host port").append(newSeparator);

        for (String s : canInvolvedMethodList(methodIndex, o)) {
            helpContentBuffer.append(s).append(newSeparator);
        }
        return helpContentBuffer.toString();
    }

    private static List<String> canInvolvedMethodList(String methodIndex, Object o) {
        List<String> list = new ArrayList<String>();
        if (o != null) {

            for (Method m : o.getClass().getMethods()) {
                if (m.getName().startsWith(methodIndex) && m.getReturnType() == String.class
                        && m.isAnnotationPresent(MethodDescribe.class)) {
                    MethodDescribe md = m.getAnnotation(MethodDescribe.class);
                    list.add(m.getName() + "\t" + md.method_name() + "\t" + md.method_param());
                }
            }
        }
        return list;

    }

    private static List<String> canInvolvedMethod(String methodIndex, Object o) {
        List<String> list = new ArrayList<String>();
        if (o != null) {

            for (Method m : o.getClass().getMethods()) {
                if (m.getName().startsWith(methodIndex) && m.getReturnType() == String.class
                        && m.isAnnotationPresent(MethodDescribe.class)) {

                    list.add(m.getName());
                }
            }
        }
        return list;

    }

    /*
     * 命令内部类
     */
    static class commandClass {
        private final String methodName;
        private final Map<String, String> arg;

        public commandClass(String methodName, Map<String, String> arg) {

            this.methodName = methodName;
            this.arg = arg;
        }

        public String getMethodName() {
            return methodName;
        }

        public Map<String, String> getArg() {
            return arg;
        }

        public String getCommandLine() {
            StringBuffer buffer = new StringBuffer();
            buffer.append(methodName).append(" ");
            if (arg != null) {
                for (Entry<String, String> e : arg.entrySet()) {
                    buffer.append("-").append(e.getKey()).append(" ").append(e.getValue()).append(" ");
                }
            }
            return buffer.toString().trim();
        }

    }

    /**
     * 解析命令行
     * 
     * @return
     */
    public static commandClass explainCommandLint(String commandLine) {
        String[] args = commandLine.split(" ");
        if (args == null || args.length % 2 != 1) {
            return null;
        } else {
            String command = args[0];
            Map<String, String> map = new HashMap<String, String>();
            try {
                for (int i = 1; i < args.length; i = i + 2) {
                    String arg = args[i].trim();
                    String argValue = args[i + 1].trim();
                    if (arg.matches("^-[a-zA-Z]+[_a-zA-Z0-9]*$") == false) {
                        return null;
                    } else {
                        map.put(arg.substring(1, arg.length()), argValue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new commandClass(command, map);
        }
    }

    private static boolean hasMethod(String methodName, Object object) {
        List<String> methodList = canInvolvedMethod(methodIndex, object);
        for (String m : methodList) {
            if (methodName.equals(m)) {
                return true;
            }
        }
        return false;
    }

    private static String exeObjectMethod(commandClass command, Object object) {
        try {
            Method m = object.getClass().getMethod(command.getMethodName(), new Class[] { Map.class });
            if (m != null) {
                return (String) m.invoke(object, command.getArg());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    private static void exeCommand(commandClass command, Socket st, Object object) throws Exception {
        String result = "";
        if (command == null || "help".equals(command.getMethodName())
                || hasMethod(command.getMethodName(), object) == false) {
            result = help(object);
        } else {
            result = exeObjectMethod(command, object);
        }
        try {

            byte[] b = result.getBytes();
            st.getOutputStream().write(b);
            st.getOutputStream().write(0x00);
            st.getOutputStream().flush();

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                st.getOutputStream().close();
            } catch (Exception e) {
            }
            try {
                st.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                st.close();
            } catch (Exception e) {
            }
        }

    }

    private static String exeCommand(commandClass command) throws Exception {
        if (command == null) {
            command = new commandClass("help", new HashMap<String, String>());
        }
        Properties prop = loadProperties();

        Socket st = null;
        try {
            String host = prop.getProperty("h");
            int port = Integer.parseInt(prop.getProperty("p"));

            if (command.getArg().containsKey("h")) {
                host = command.getArg().get("h");
            }
            if (command.getArg().containsKey("p")) {
                port = Integer.parseInt(command.getArg().get("p"));
            }

            st = new Socket();
            st.connect(new InetSocketAddress(host, port));

            byte[] b = command.getCommandLine().getBytes();
            st.getOutputStream().write(b);
            st.getOutputStream().write(0x00);
            st.getOutputStream().flush();

            System.out.println("OneObjectUtil send command " + command.getCommandLine() + " to " + host + ":" + port
                    + " success!");

            String result = readInputStream(st.getInputStream());
            return result;

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                st.getOutputStream().close();
            } catch (Exception e) {
            }
            try {
                st.getInputStream().close();
            } catch (Exception e) {
            }
            try {
                st.close();
            } catch (Exception e) {
            }
        }

    }

    public static void main(String[] args) throws Exception {
        args = new String[] { "exit" };
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; args != null && i < args.length; i++) {
            buffer.append(args[i]).append(" ");
        }
        commandClass command = explainCommandLint(buffer.toString());

        System.out.println(exeCommand(command));

    }

}
