package Design;

import Design.CustomData;
import Design.CustomLabel;

import javax.swing.*;
import java.awt.*;

public class cellRender  implements ListCellRenderer {
    private  final ImageIcon tipIcon = new ImageIcon (this.getClass().getResource("/images/tip.png"));;
    private CustomLabel renderer=new CustomLabel(tipIcon);

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        renderer.setSelected ( isSelected );
        renderer.setData((CustomData)value);
        return renderer;
    }
}
