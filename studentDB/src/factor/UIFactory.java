package factor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static factor.UIConfig.DEFAULT_FONT;

public class UIFactory {
    enum ControlType{
        LABEL,
        TEXT_FIELD,
        PASSWORD_FIELD,
        COMBO_BOX,
        BUTTON
    }

    private static UIFactory instance;

    private UIFactory() {}

    public static UIFactory getInstance() {
        if (instance == null) {
            synchronized (UIFactory.class) {
                if (instance == null) {
                    instance = new UIFactory();
                }
            }
        }
        return instance;
    }

    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConfig.LABEL_FONT);
        label.setForeground(UIConfig.PRIMARY_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return label;
    }

    public JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(UIConfig.INPUT_FONT);
        field.setBorder(createInputBorder());
        field.setPreferredSize(new Dimension(
                UIConfig.INPUT_WIDTH,
                UIConfig.INPUT_HEIGHT
        ));
        return field;
    }

    public JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UIConfig.INPUT_FONT);
        field.setBorder(createInputBorder());
        field.setPreferredSize(new Dimension(
                UIConfig.INPUT_WIDTH,
                UIConfig.INPUT_HEIGHT
        ));
        return field;
    }

    public JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UIConfig.INPUT_FONT);
        combo.setBackground(Color.WHITE);
        combo.setFocusable(false);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        return combo;
    }

    //取消第二个颜色参数
    public JButton createButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 背景颜色调整为浅灰色系
                if (getModel().isPressed()) {
                    g2.setColor(new Color(200, 200, 200)); // 按下状态
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(240, 240, 240)); // 悬停状态
                } else {
                    g2.setColor(Color.WHITE);          // 默认状态
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 180, 180)); // 中灰色边框
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
                g2.dispose();
            }
        };

        // 文字颜色调整
        button.setForeground(Color.BLACK);
        button.setFont(DEFAULT_FONT);

        // 其他样式保持不变
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(8, 20, 8, 20));

        button.setPreferredSize(new Dimension(100,30));

        return button;
    }

    private Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConfig.INPUT_BORDER),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private Border createButtonBorder(Color base) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(base.darker()),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        );
    }

    private void setupHoverEffect(JButton button, Color base) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(base);
            }
        });
    }

//    public Component createControl(ControlType type, Object... args)
//    {
//        switch (type) {
//            case LABEL:
//                return createLabel((String) args[0]);
//        case TEXT_FIELD:
//            return createTextField();
//        case PASSWORD_FIELD:
//            return createPasswordField();
//        case COMBO_BOX:
//            return createComboBox((String[]) args);
//        case BUTTON:
//            return createButton((String) args[0], Color.BLACK);
//        default:
//            return null;
//        }
//    }

}
