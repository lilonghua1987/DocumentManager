/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilonghua.Apllication;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 * @author wpf
 */
public class TableEditeModel extends JTextField {

    private JPopupMenu jPopupMenu = new JPopupMenu();
    private JMenuItem copy = new JMenuItem("复制");
    // private JMenuItem paste = new JMenuItem("粘贴");  
    // private JMenuItem cut = new JMenuItem("剪切");  
    private JMenuItem openUrl = new JMenuItem("打开网页");
    private boolean browse = false;
    TableEditeModel myself = this;

    public TableEditeModel(final boolean browse) {
        this.browse = browse;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                if (arg0.getButton() == 3) {
                    if (myself.isEnabled()) {//如果当前组件处于不可用状态，则不弹出右键菜单  

                        jPopupMenu.removeAll();
                        if (!myself.isEditable()) {
                            jPopupMenu.add(copy);
                        } else {
                            jPopupMenu.add(copy);
                            //jPopupMenu.add(paste);  
                            //jPopupMenu.add(cut);
                            if (browse) {
                                jPopupMenu.add(openUrl);
                            }
                        }
                        jPopupMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
                    }
                }
            }
        });

        jPopupMenu.add(copy);
        //jPopupMenu.add(paste);  
        //jPopupMenu.add(cut);
        if (browse) {
            jPopupMenu.add(openUrl);
        }

        //copy.setIcon(ImageIcons.copy_gif);  
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                myself.copy();
            }
        });
        //paste.setIcon(ImageIcons.paste_png);  
//        paste.addActionListener(new ActionListener(){  
//            @Override
//            public void actionPerformed(ActionEvent arg0) {  
//                myself.paste();       
//            }         
//        });  
        //cut.setIcon(ImageIcons.cut_png);  
//        cut.addActionListener(new ActionListener(){  
//            @Override
//            public void actionPerformed(ActionEvent arg0) {  
//                myself.cut();         
//            }         
//        }); 

        openUrl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String osName = System.getProperty("os.name");
                String url = myself.getSelectedText();
                try {
                    if (osName.startsWith("Mac OS")) {
                        //doc
                        Class fileMgr = Class.forName("com.apple.eio.FileManager");
                        Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                        openURL.invoke(null, new Object[]{url});
                    } else if (osName.startsWith("Windows")) {
                        //Windows
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                    } else {
                        //assume Unix or Linux
                        String[] browsers = {
                            "firefox", "opera", "konqueror",
                            "epiphany", "mozilla", "netscape"};
                        String browser = null;
                        for (int count = 0; count < browsers.length && browser == null; count++) {
                            if (Runtime.getRuntime().exec(
                                    new String[]{"which", browsers[count]}).waitFor() == 0) {
                                browser = browsers[count];
                            }
                        }
                        if (browser != null) {
                            Runtime.getRuntime().exec(new String[]{browser, url});
                        }
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException | InterruptedException ex) {
                }
//                String cmd = "cmd.exe /c start ";
//
//                //构造本地文件路径或者网页URL  
//                //String file = "http://www.baidu.com";  
//                //String file = "C:/Users/Wentasy/Desktop/core_java_3_api/index.html";
//
//                try {
//                    //执行操作  
//                    System.out.println(myself.getSelectedText());
//                    Runtime.getRuntime().exec(cmd + myself.getSelectedText());
//                } catch (IOException ignore) {
//                    //打印异常  
//                    ignore.printStackTrace();
//                }
            }
        });
    }
}
