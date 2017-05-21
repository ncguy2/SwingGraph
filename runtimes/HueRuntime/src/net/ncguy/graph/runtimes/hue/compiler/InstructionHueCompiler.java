package net.ncguy.graph.runtimes.hue.compiler;

import net.ncguy.graph.runtimes.hue.library.factories.SendInstructionsNode;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InstructionHueCompiler  {

    public void CompileNode(SendInstructionsNode rootNode) {
        InetAddress targetAddress = null;
        try {
            Object val = rootNode.GetValueFromInputPin(rootNode.targetHostPin);
            if(val instanceof InetAddress)
                targetAddress = (InetAddress) val;
            else targetAddress = InetAddress.getByName(val.toString().substring(1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int targetPort = (int) rootNode.GetValueFromInputPin(rootNode.targetPortPin);
        SendInstructionsNode.Commands instruction = (SendInstructionsNode.Commands) rootNode.GetValueFromInputPin(rootNode.instructionIdPin);
        Color colourA = (Color) rootNode.GetValueFromInputPin(rootNode.colourAPin);
        Color colourB = (Color) rootNode.GetValueFromInputPin(rootNode.colourBPin);
        Color colourC = (Color) rootNode.GetValueFromInputPin(rootNode.colourCPin);
        Color colourD = (Color) rootNode.GetValueFromInputPin(rootNode.colourDPin);

        Color[] colours = new Color[]{
                colourA,
                colourB,
                colourC,
                colourD
        };

        byte[] sb = new byte[15];
        sb[0] = instruction.id;
        for(int i = 0; i < colours.length; i++)
            AppendColourBytes(sb, (i * 3) + 1, colours[i]);
        sb[sb.length-1] = (byte)'\0';

        try {
            Send(targetAddress, targetPort, sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void AppendColourBytes(byte[] sb, int offset, Color c) {

        int r = Math.max(1, c.getRed());
        int g = Math.max(1, c.getGreen());
        int b = Math.max(1, c.getBlue());

        byte br = (byte)r;
        byte bg = (byte)g;
        byte bb = (byte)b;

        sb[offset + 0] = br;
        sb[offset + 1] = bg;
        sb[offset + 2] = bb;
    }

    public byte[] BuildByteBuffer(char[] payload) {
        byte[] buffer = new byte[payload.length];
        for (int i = 0; i < payload.length; i++)
            buffer[i] = (byte) payload[i];
        return buffer;
    }

    private static void Send(InetAddress targetHost, int targetPort, byte[] payload) throws IOException {
        DatagramPacket pkt = new DatagramPacket(payload, payload.length, targetHost, targetPort);
        DatagramSocket skt = new DatagramSocket();
        skt.send(pkt);
        skt.close();
        System.out.println("UDP packet sent to " + ToFullTarget(targetHost, targetPort) + " with a payload size of " + payload.length);
    }

    public static String ToFullTarget(InetAddress target, int port) {
        String sb = target.toString() + ":" + port;
        return sb;
    }



}
