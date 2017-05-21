package net.ncguy.graph.data;

import aurelienribon.tweenengine.Tween;
import net.ncguy.graph.data.icons.Icons;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.scene.render.SceneGraphForm;
import net.ncguy.graph.tween.JComponentTweenAccessor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class MutablePropertyControlRegistry {

    private static MutablePropertyControlRegistry instance;
    public static MutablePropertyControlRegistry instance() {
        if (instance == null)
            instance = new MutablePropertyControlRegistry();
        return instance;
    }

    private MutablePropertyControlRegistry() {
        typeBuilders = new HashMap<>();
    }

    protected final Map<Class<?>, JComponentBuilder> typeBuilders;

    public <V, T extends MutableProperty<V>, U extends JComponent> void RegisterBuilder(Class<V> type, JComponentBuilder<T, U> builder) {
        typeBuilders.put(type, builder);
    }

    public <T> JComponentBuilder GetBuilder(Class<T> type) {
        if(typeBuilders.containsKey(type))
            return typeBuilders.get(type);
        new ToastEvent("No builder found with type " + type.getCanonicalName()).setImagePath(Icons.Icon.WARNING_WHITE).fire();
        return null;
    }

    public <T, U extends JComponent> U Build(MutableProperty<T> property) {
        return Build(property.getTypeClass(), property);
    }

    public <T, U extends JComponent> U Build(Class<T> type, MutableProperty<T> property) {
        JComponentBuilder builder = GetBuilder(type);
        if(builder == null) {
            new ToastEvent("Unable to build control for type " + type.getCanonicalName()).setImagePath(Icons.Icon.WARNING_WHITE).fire();
            return (U) new JLabel("Error");
        }
        return (U) builder.Build(property);
    }

    public void Defaults() {
        JComponentBuilder<MutableProperty<Float>, JSpinner> floatBuilder = property -> {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setValue(property.get());
            JSpinner spinner = new JSpinner(model);
            spinner.addChangeListener(e -> property.set((Float) model.getValue()));
            return spinner;
        };
        JComponentBuilder<MutableProperty<Integer>, JSpinner> intBuilder = property -> {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setValue(property.get());
            JSpinner spinner = new JSpinner(model);
            spinner.addChangeListener(e -> property.set((Integer) model.getValue()));
            return spinner;
        };
        JComponentBuilder<MutableProperty<Long>, JSpinner> longBuilder = property -> {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setValue(property.get());
            JSpinner spinner = new JSpinner(model);
            spinner.addChangeListener(e -> property.set((Long) model.getValue()));
            return spinner;
        };

        RegisterBuilder(Float.class, floatBuilder);
        RegisterBuilder(Float.TYPE, floatBuilder);
        RegisterBuilder(Integer.class, intBuilder);
        RegisterBuilder(Integer.TYPE, intBuilder);
        RegisterBuilder(Long.class, longBuilder);
        RegisterBuilder(Long.TYPE, longBuilder);
        RegisterBuilder(String.class, property -> {
            JTextField field = new JTextField(property.get());
            ListenerHelpers.addChangeListener(field, e -> property.set(field.getText()));
            return field;
        });

        RegisterBuilder(Color.class, property -> {
            JPanel bg = new JPanel();
            bg.setLayout(new BorderLayout());
            JButton btn = new JButton("Set Colour");
            bg.add(btn, BorderLayout.CENTER);
            bg.getInsets().set(2, 2, 2, 2);
            bg.setBackground(property.get());
            btn.setBackground(property.get());
            btn.addActionListener(e -> {
                Color col = property.get();
                col = JColorChooser.showDialog(btn, "Choose a colour", col);
                if(col == null) return;
                property.set(col);

                Tween.to(btn, JComponentTweenAccessor.COLOUR, .4f).target(col.getRed(), col.getGreen(), col.getBlue()).start(SceneGraphForm.instance.tweenManager);
                Tween.to(bg, JComponentTweenAccessor.COLOUR, .4f).target(col.getRed(), col.getGreen(), col.getBlue()).start(SceneGraphForm.instance.tweenManager);

//                btn.setBackground(col);
//                bg.setBackground(col);
//                btn.repaint();
//                bg.repaint();
            });
            return bg;
        });

    }

    public static void FindPropertiesInInstance(Object obj, java.util.List<MutableProperty> list) throws IllegalAccessException {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if(field.getType().equals(MutableProperty.class))
                list.add((MutableProperty) field.get(obj));
        }
    }

    public static interface JComponentBuilder<T extends MutableProperty, U extends JComponent> {
        U Build(T property);
    }

    public static class DataComponentWrapper<T extends MutableProperty, U extends JComponent, V> {
        public JComponentBuilder<T, U> builder;
        public BiConsumer<T, U> dataProvider;

        public DataComponentWrapper(JComponentBuilder<T, U> builder, BiConsumer<T, U> dataProvider) {
            this.builder = builder;
            this.dataProvider = dataProvider;
        }
    }


    public static class ListenerHelpers {
        public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
            Objects.requireNonNull(text);
            Objects.requireNonNull(changeListener);
            DocumentListener dl = new DocumentListener() {
                private int lastChange = 0, lastNotifiedChange = 0;

                @Override
                public void insertUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    changedUpdate(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    lastChange++;
                    SwingUtilities.invokeLater(() -> {
                        if (lastNotifiedChange != lastChange) {
                            lastNotifiedChange = lastChange;
                            changeListener.stateChanged(new ChangeEvent(text));
                        }
                    });
                }
            };
            text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
                Document d1 = (Document)e.getOldValue();
                Document d2 = (Document)e.getNewValue();
                if (d1 != null) d1.removeDocumentListener(dl);
                if (d2 != null) d2.addDocumentListener(dl);
                dl.changedUpdate(null);
            });
            Document d = text.getDocument();
            if (d != null) d.addDocumentListener(dl);
        }
    }

}
