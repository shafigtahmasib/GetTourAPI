package com.example.gettour_api.utils;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ImageUtil {

    /**
     This method is used for taking list of elements and writing them on the picture
     */

    public static BufferedImage writeTextOnImage(List<String> text) throws IOException {
        int linePosition1 = 180;
        int linePosition2 = 35;
        BufferedImage bufferedImage = new BufferedImage(800, 300, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.green);
        graphics.fillRect(0, 0, 800, 300);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial Black", Font.BOLD, 20));
        graphics.drawString("Price:", 10, 35);
        graphics.drawString("Date Interval:", 10, 70);
        graphics.drawString("Description:", 10, 105);
        graphics.drawString("Get Tour", 690, 290);
        for(String line: text) {
            if(line.length()>100){
                int count = line.length()/50;
                while(count!=0){
                    graphics.drawString(line.substring(0, 50), linePosition1, linePosition2);
                    linePosition2 += 25;
                    line=line.substring(50);
                    if(line.length()<50){
                        graphics.drawString(line, linePosition1, linePosition2);
                        linePosition2 += 25;
                    }
                    count--;
                }
            }
            else {
                graphics.drawString(line, linePosition1, linePosition2);
                linePosition2 += 35;
            }
        }
        return bufferedImage;
    }

    /**
     This method helps to take image and convert it to the byte array
     */

    public static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return out.toByteArray();
    }
}
