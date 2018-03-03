import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class utils {

    /**
     * Exemplar main method
     */
    public static void main(String[] args) {
        String workingFolder = System.getProperty("user.dir");

        final int maxFloors = 3;
        final int maxZoom = 8;
        final int tileWidth = 256;

        String populated = splitImages(args.length > 0 ? args[0] : workingFolder, workingFolder, "Map", "TileMapsFoldered\\TileMap", maxFloors, maxZoom, tileWidth);
        System.out.print("Images Split Correctly");
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("PopulatedFiles")));
            bufferedOutputStream.write(populated.getBytes("UTF-8"));
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and splits up each image into its requisite tiles. Requires input images to be powers of 2 (1, 2, 4, 8, ...) * tileWidth.
     *
     * @param srcPath        the path to the top level directory of the maps folder (should be one flat hierarchy)
     * @param destPath       the resultant path of the files
     * @param baseSrcString  the base format of the input files taking an two integers floor and zoom
     * @param baseDestString the base format of the output files
     * @param maxFloors      the maximum number of floors
     * @param maxZoom        the maximum zoom level
     * @param tileWidth      the width of each tile
     * @return returns a String containing all populated tiles in the format "(<floor>,<zoom>,<x>,<y>);"
     */
    private static String splitImages(String srcPath, String destPath, String baseSrcString, String baseDestString, int maxFloors, int maxZoom, int tileWidth) {
        StringBuilder res = new StringBuilder();
        for (int f = 2; f < maxFloors; f++) {
            for (int z = 0; z < maxZoom; z++) {
                try {
                    //load in each row of tiles into memory before splitting to increase efficiency of memory access
                    File file = new File(String.format(srcPath + "\\" + baseSrcString, (f + 1), z));
                    ImageReader imageReader = ImageIO.getImageReadersBySuffix("png").next();
                    imageReader.setInput(ImageIO.createImageInputStream(file), false);
                    for (int y = 0; y < Math.pow(2, z); y++) {
                        Rectangle rowRect = new Rectangle(0, y * tileWidth, imageReader.getWidth(0), tileWidth);
                        ImageReadParam paramRow = imageReader.getDefaultReadParam();
                        paramRow.setSourceRegion(rowRect);
                        BufferedImage xRow = imageReader.read(0, paramRow);
                        for (int x = 0; x < Math.pow(2, z); x++) {
                            BufferedImage img = xRow.getSubimage(x * tileWidth, 0, tileWidth, tileWidth);
                            if (!allWhite(img)) {
                                File destFile = new File(destPath + "\\" + baseDestString + (f + 1) + "\\" + z + "\\" + x + "\\" + y + ".png");
                                destFile.getParentFile().mkdirs();
                                ImageIO.write(img, "png", destFile);

                                //append correct file to allow for return of populated files
                                res.append(String.format("(%d,%d,%d,%d);", f, z, x, y));
                            }
                        }
                        System.out.print("\r" + (y + 1));
                    }
                    System.out.print("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return res.toString();
    }

    /**
     * @param img the image to analyse
     * @return Returns true if the image is all white i.e. blank
     */
    private static boolean allWhite(BufferedImage img) {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if (img.getRGB(i, j) != Color.white.getRGB()) {
                    return false;
                }
            }
        }
        return true;
    }
}
