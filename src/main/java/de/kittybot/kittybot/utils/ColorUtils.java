package de.kittybot.kittybot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ColorUtils{

	private static final Logger LOG = LoggerFactory.getLogger(ColorUtils.class);

	public static byte[] generateColorImage(Color color){
		try{
			var image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			var g = image.createGraphics();

			g.setColor(color);
			g.fillRect(0, 0, 100, 100);

			var baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
		}
		catch(IOException e){
			LOG.error("Error while creating color image", e);
		}
		return new byte[]{};
	}

}
