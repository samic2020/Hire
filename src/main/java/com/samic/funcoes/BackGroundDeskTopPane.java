package com.samic.funcoes;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

/**
 *
 * @author YOGA 510
 */
public class BackGroundDeskTopPane extends JDesktopPane {
     private ImageIcon image;
     private MediaTracker tracker;

     public BackGroundDeskTopPane(String caminhoImagem) {

        if (caminhoImagem.isEmpty()) {
            URL url = getClass().getResource("/figuras/BackGround.png");
            image = new ImageIcon(url);
        } else {
            String url = caminhoImagem;
            image = new ImageIcon(url);
        }
                
         tracker = new MediaTracker(this);
         tracker.addImage(image.getImage(), 0);

         try {
             tracker.waitForID(0);
         } catch (InterruptedException exception) {
             exception.printStackTrace();
         }
    }

     public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dDesktop = this.getSize();

        double width = dDesktop.getWidth();
        double height = dDesktop.getHeight();

        Image background = new ImageIcon(image.getImage().getScaledInstance(
                (int) width, (int) height, 1)).getImage();

        g.drawImage(background, 0, 0, this);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
}
