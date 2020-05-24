package com.sl.chat.renderer;

import com.sl.chat.bean.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 消息列表渲染
 */
public class MessageRenderer implements ListCellRenderer<Message> {
    private ArrayList<Holder> holders = new ArrayList<>();
    private int id =-1;
    public MessageRenderer(int id){
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Message> list, Message value, int index, boolean isSelected, boolean cellHasFocus) {
        //说明是新元素
        if (index >= holders.size()){
            Message msg = list.getModel().getElementAt(index);
            Holder holder = new Holder();
            holder.title = new JLabel(msg.getHeader());
            holder.content = new JLabel(msg.getMsg());
            if (msg.getSource().getId() == -1) {
                holder.setBackground(Color.RED);
            }else if (msg.getSource().getId() == id){
                holder.setBackground(Color.CYAN);
            }
            holder.addAll();
            holders.add(holder);
            return holder;
        }else {
            return holders.get(index);
        }
    }

    private class Holder extends JPanel{
        private JLabel title,content;
        public Holder(){
            this(null);
        }
        public Holder(LayoutManager layout) {
            //两行一列，标题加内容
            super(new GridLayout(2,1));
        }
        public void addAll(){
            add(title);
            add(content);
        }
    }
}
