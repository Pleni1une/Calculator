import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Stack;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;


public class calculator extends JFrame {//进行数据运算
    static private final Stack<Double> osValue = new Stack<>(); // 值  表操作数
    static private final Stack<String> osChar = new Stack<>(); // 运算符
    static public int R=255,B=255,G=255;//初始化颜色
    static private void calculate(){
        String b = osChar.pop();
        double c = osValue.pop();
        double d = osValue.pop();
        double e; //结果
        switch (b) {
            case "+" -> {
                e = d + c;
                osValue.push(e);
            }
            case "-" -> {
                e = d - c;
                osValue.push(e);
            }
            case "*" -> {
                e = d * c;
                osValue.push(e);
            }
            case "/" -> {
                if (c == 0)
                    throw new ArithmeticException("除0错误!");
                e = d / c;
                osValue.push(e);
            }
            case "%" -> {
                if (c == 0)
                    throw new ArithmeticException("除0错误！");
                else {
                    int c1 = (int) c;
                    int d1 = (int) d;//化为整数 整数才能求余
                    if (c1 == c && d1 == d) {
                        e = d1 % c1;
                        osValue.push(e);
                    } else
                        throw new ArithmeticException("浮点数不能进行求余运算！");
                }
            }
            case "^" -> {
                int c1 = (int) c;
                if (c1 == c) {
                    e = Math.pow(d, c);
                    osValue.push(e);
                } else
                    throw new ArithmeticException("幂方不能为浮点数！");

            }
            //科学计数法
            case "e" -> {
                int c1 = (int) c;
                if (c1 == c) {
                    e = d * Math.pow(10, c1);
                    osValue.push(e);
                }
            }
        }
    }
    static private Double calculate(String text){//数据处理、数据获取
        HashMap<String, Integer> operator = new HashMap<>();//字典、映射关系
        osValue.push(0.0); //设置一个操作数的初值为0
        operator.put("(",0);
        operator.put(")",0);
        operator.put("e",2);
        operator.put("^",2);
        operator.put("/",2);
        operator.put("*",2);
        operator.put("%",2);
        operator.put("-",1);
        operator.put("+",1);
        operator.put("#",0);  //设置优先级

        osChar.push("#");//结束标记

        int flag = 0;//记录操作数的头
        int n = text.length();
        for(int i = 0; i < n; i++) {
            String a = String.valueOf(text.charAt(i)); //一个字符一个字符的进行查找 （导致只能使用单字符符号）不能用sin那些
            if (!a.matches("[0-9.]")) {//当a不是数字或者.时
                if(!operator.containsKey(a))
                    throw new ArithmeticException("计算式错误");//避免非法字符输入
                if(i<n-1&& operator.get(a)>1){
                    String b = String.valueOf(text.charAt(i+1));
                    if(!b.matches("[0-9.]")&&operator.get(b)>0)
                        throw new ArithmeticException("计算式错误");
                }
                if(flag != i) {
                    osValue.push(Double.parseDouble(text.substring(flag, i)));//将操作数推入osValue中
                }
                flag = i + 1;
                while(!(a.equals("#") && osChar.peek().equals("#"))) { // peek 返回栈顶元素，不删除
                    if(operator.get(a) > operator.get(osChar.peek()) || a.equals("(")) {
                        osChar.push(a); // 加入操作符
                        break;
                    } else {
                        if(a.equals(")")) {
                            while(!osChar.peek().equals("("))
                                calculate();
                            osChar.pop();
                            break;
                        }
                        calculate();
                    }
                }

            }
        }
        return osValue.pop();
    }

    calculator() {

        //使用重构的方法 构建计算器
        JFrame f =new JFrame();
        Toolkit took = Toolkit.getDefaultToolkit();
        Image image = took.getImage("src/main/resources/ico.ico");
        setIconImage(image);
        f.setSize(400,600);
        
        //字体相关
        Font font = new Font("msyh", Font.PLAIN, 11);
        font = font.deriveFont(font.getSize() * (float) 2.5);

        //第三方主题flatlaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(f);
        } catch (Exception ignored) {
        }


        //板块1  菜单栏
        JMenuBar menu=new JMenuBar();  //菜单条

        JMenu jMenu0=new JMenu("设置");
        JMenuItem tc=new JMenuItem("退出");
        tc.addActionListener(e-> System.exit(0));  //设置退出
        jMenu0.add(tc);

        JMenuItem hf=new JMenuItem("恢复默认设置");
        jMenu0.add(hf);
        menu.add(jMenu0);

        JMenu jMenu1=new JMenu("设置主题");
        JMenuItem item1=new JMenuItem("浅色主题");
        JMenuItem item2=new JMenuItem("深色主题");
        jMenu1.add(item1);
        jMenu1.add(item2);
        menu.add(jMenu1);

        JMenu jMenu2=new JMenu("设置字体");
        JMenuItem Font1=new JMenuItem("华文琥珀");
        JMenuItem Font2=new JMenuItem("华文新魏");
        JMenuItem Font3=new JMenuItem("华文彩云");
        JMenuItem Font4=new JMenuItem("微软雅黑");
        jMenu2.add(Font1);
        jMenu2.add(Font2);
        jMenu2.add(Font3);
        jMenu2.add(Font4);
        menu.add(jMenu2);
        f.setJMenuBar(menu);

        //板块2  文本框
        JTextArea text=new JTextArea(100,10);
        text.setLineWrap(true);  //相当于自动换行（取消横向方向的滚动条）
        text.setFont(font);
        text.setCaretPosition(text.getDocument().getLength());
        JScrollPane m0 = new JScrollPane(text); //带滚动条的面板（JPanel）
        m0.setBounds(0,0,f.getWidth()-12, 100);//绝对地址+尺寸   (但好像xy的值不会影响成果)
        f.add(m0);

        //板块3  数字框 + 运算符
        JPanel m1=new JPanel();
        m1.setLayout(new GridLayout(0, 4)); //设置面板每列的按钮个数  （实现分层）
        String[] s={
                "C","(",")","Del",
                "e","^","%","/",
                "7","8","9","*",
                "4","5","6","-",
                "1","2","3","+",
                "+/-","0",".","="
        };
        JButton[] an=new JButton[s.length];
        for(int i=0;i<s.length;i++)
        {
            an[i]= new JButton(s[i]);// 宽度100像素, 高度50像素
            an[i].setFont(font);
            m1.add(an[i]);
        }
        an[s.length-1].setBackground(Color.lightGray);
        f.getContentPane().add(m1,BorderLayout.SOUTH); //表方位，一定要加，否则会覆盖

        //板块4  设置功能
        for(int i=1;i<s.length-1;i++) //取消=的插入
        {//用鼠标把数据放到text中
            if(i==20 || i==3)
                continue;
            int j=i;
            an[i].addActionListener(e->text.append(s[j])); //必须用j=i来给text末尾加上数字 直接用i行不通
        }

        //特殊功能按钮设置
        an[look_s(s,"C")].addActionListener(e->{
            text.setText("");//把文本框清空
            osChar.clear();
            osValue.clear();//把所有的东西都清空
        });

        //取相反数 
        an[look_s(s,"+/-")].addActionListener(e->{
            String t1=text.getText();
            if(t1.startsWith("-"))//表判断是不是以"-"开头  要和运算中的减号区分开 不然总是说运算出差
                text.setText(text.getText().substring(1));//截取1到最后（去掉-号）
            else
                text.setText("-"+text.getText());
        });

        an[look_s(s, "Del")].addActionListener(e->{
            try {
                text.setText(text.getText().substring(0,text.getText().length()-1));
            } catch (Exception ignored) {

            }
        });

        an[look_s(s, "=")].addActionListener(e->{
            try{
                System.out.println(text.getText());//输出text中的数
                double x = calculate(text.getText()+"#"); //加“#”使无运算符时 可以正常输出当前结果
                text.setText("");//清空文本
                text.append(String.valueOf(x)); //输入结果
            }catch(Exception cw){
                //运算过程可能会出错（需要捕捉错误）
                if(cw.getMessage()==null)
                {
                    text.setText("计算错误！");
                }
                else
                    text.setText(cw.getMessage());
            }
        }); 

        //板块5  菜单功能设置
        SetColor(m0,text,an,50);
        hf.addActionListener(e->{
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                SwingUtilities.updateComponentTreeUI(f);
            } catch (Exception ignored) {
            }
            SetColor(m0,text,an,50);
            SetFont(text, an,3);
        });


        item1.addActionListener(e->{
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
                SwingUtilities.updateComponentTreeUI(f);
            } catch (Exception ignored) {
            }
        });
        item2.addActionListener(e->{
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                SwingUtilities.updateComponentTreeUI(f);
            } catch (Exception ignored) {
            }
        });


        Font1.addActionListener(e-> SetFont(text,an,0));
        Font2.addActionListener(e-> SetFont(text,an,1));
        Font3.addActionListener(e-> SetFont(text,an,2));
        Font4.addActionListener(e-> SetFont(text,an,3));

        //板块6  设置窗口属性（与可见）
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);  //和窗口的布局有关
        f.setVisible(true);
    }

    //颜色设置子函数
    static public void SetColor(JScrollPane m, JTextArea a, JButton[] an, int n){
        m.setBackground(new Color(R,G,B));
        a.setBackground(new Color(R,G,B));

        for (JButton jButton : an) {
            if ("=".equals(jButton.getText()))
                jButton.setBackground(new Color(R - n, G - n, B - n));
            else
                jButton.setBackground(new Color(R, G, B));
        }
    }

    //字体设置子函数
    static public void SetFont(JTextArea a, JButton[] an, int n){
        String[] s={"STHUPO","STXINWEI","STCAIYUN","msyh"};
        Font font = new Font(s[n], Font.PLAIN, 11);
        font = font.deriveFont(font.getSize() * (float) 2.5);// 设置行高为2.5倍
        a.setFont(font);
        for (JButton jButton : an) {
            jButton.setFont(font);
            jButton.setSize(50, 50);
        }
    }
    static public int look_s(String[] s,String s1){
        int i;
        for(i=0;i<s.length;i++)
        {
            if(s[i].equals(s1))
                return i;
        }
        return -1;
    }
    static public void main(String[] arg)
    {
        new JFrame();
        new calculator();
    }
}