package net.ncguy.graph.runtimes.hue.library;

import net.ncguy.graph.data.MutablePropertyControlRegistry;
import net.ncguy.graph.runtime.api.IRuntimeLibrary;
import net.ncguy.graph.runtimes.hue.library.factories.DelayNode;
import net.ncguy.graph.runtimes.hue.library.factories.SendInstructionsNode;
import net.ncguy.graph.runtimes.hue.library.factories.SendInstructionsNode.Commands;
import net.ncguy.graph.scene.logic.factory.NodeFactory;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HueLibrary implements IRuntimeLibrary {

    @Override
    public List<NodeFactory> getNodeFactories() {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new SendInstructionsNode.SendInstructionFactory());
        factories.add(new DelayNode.DelayFactory());
        return factories;
    }

    @Override
    public void RegisterControlAdapters() {
        MutablePropertyControlRegistry.instance().RegisterBuilder(Commands.class, property -> {
            JComboBox<Commands> box = new JComboBox<>(Commands.values());
            box.setSelectedIndex(property.get().ordinal());
            box.addActionListener(e -> property.set((Commands) box.getSelectedItem()));
            return box;
        });


        MutablePropertyControlRegistry.instance().RegisterBuilder(InetAddress.class, property -> {
            JFormattedTextField field = new JFormattedTextField(new IPAddressFormatter());
            field.setText(property.get().toString().substring(1));
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        property.set(InetAddress.getByName(field.getText()));
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            return field;
        });
        MutablePropertyControlRegistry.instance().RegisterBuilder(Inet4Address.class, property -> {
            JFormattedTextField field = new JFormattedTextField(new IPAddressFormatter());
            field.setText(property.get().toString().substring(1));
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        property.set((Inet4Address) InetAddress.getByName(field.getText()));
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            return field;
        });
    }

    class IPAddressFormatter extends DefaultFormatter
    {
        public String valueToString(Object value) throws ParseException
        {
            if (!(value instanceof byte[])) throw new ParseException("Not a byte[]", 0);
            byte[] a = (byte[]) value;
            if (a.length != 4) throw new ParseException("Length != 4", 0);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 4; i++)
            {
                int b = a[i];
                if (b < 0) b += 256;
                builder.append(String.valueOf(b));
                if (i < 3) builder.append('.');
            }
            return builder.toString();
        }

        public Object stringToValue(String text) throws ParseException
        {
            StringTokenizer tokenizer = new StringTokenizer(text, ".");
            byte[] a = new byte[4];
            for (int i = 0; i < 4; i++)
            {
                int b = 0;
                if (!tokenizer.hasMoreTokens()) throw new ParseException("Too few bytes", 0);
                try
                {
                    b = Integer.parseInt(tokenizer.nextToken());
                }
                catch (NumberFormatException e)
                {
                    throw new ParseException("Not an integer", 0);
                }
                if (b < 0 || b >= 256) throw new ParseException("Byte out of range", 0);
                a[i] = (byte) b;
            }
            if (tokenizer.hasMoreTokens()) throw new ParseException("Too many bytes", 0);
            return a;
        }
    }

}
