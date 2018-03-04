import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class utils {

    /**
     * Exemplar main method
     */
    public static void main(String[] args) {
//        String workingFolder = System.getProperty("user.dir");
//
//        String populated = splitImages("Final Maps",
//                workingFolder, "Map%d_%d.png", "TileMapsFoldered\\TileMap%d\\%d\\%d\\%d.png", 2, 3, 7, 256);
//        System.out.print("Images Split Correctly");
//        try {
//            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("PopulatedFiles")));
//            bufferedOutputStream.write(populated.getBytes("UTF-8"));
//            bufferedOutputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        genBlankTile("TileMapsNewFoldered", 256, Color.WHITE);

        tileImage("Map1_7.png", "TileMap1\\7\\%d\\%d.png", 7, 256);

        try {
            String format = "TileMap%d\\%d\\%d\\%d.png";
            for(int zoom = 6; zoom >= 0; zoom --) {
                for (int f = 1; f <= 1; f++) {
                    for (int x = 0; x < Math.pow(2, zoom); x++) {
                        for (int y = 0; y < Math.pow(2, zoom); y++) {
                            int ax = x * 2, ay = y * 2;
                            int bx = x * 2 + 1, by = y * 2;
                            int cx = x * 2, cy = y * 2 + 1;
                            int dx = x * 2 + 1, dy = y * 2 + 1;

                            BufferedImage A = loadImageFromFile(blankOrFile(format, f, zoom + 1, ax, ay));
                            BufferedImage B = loadImageFromFile(blankOrFile(format, f, zoom + 1, bx, by));
                            BufferedImage C = loadImageFromFile(blankOrFile(format, f, zoom + 1, cx, cy));
                            BufferedImage D = loadImageFromFile(blankOrFile(format, f, zoom + 1, dx, dy));

                            BufferedImage result = combineImageImage(A, B, C, D);

                            File resultFile = new File(String.format(format, f, zoom, x, y));
                            resultFile.getParentFile().mkdirs();

                            if (!allWhite(result))
                                ImageIO.write(result, "png", new File(String.format(format, f, zoom, x, y)));
                            System.out.print(String.format("\rz=%d/%d, f=%d/%d, x=%d/%d, y=%d/%d", 7-zoom, 7, f, 3, x, (int)Math.pow(2, zoom), y, (int)Math.pow(2, zoom)));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("\n");

    }

    private static File blankOrFile(String format, int f, int zoom, int x, int y){
        File res = new File(String.format(format, f, zoom, x, y));
        if(!res.exists())
            res = new File("blank.png");
        return res;
    }

    private static BufferedImage loadImageFromFile(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * Loads and splits up each image into its requisite tiles. Requires input images to be powers of 2 (1, 2, 4, 8, ...) * tileWidth.
     *
     * @param srcPath        the path to the top level directory of the maps folder (should be one flat hierarchy)
     * @param destPath       the resultant path of the files
     * @param baseSrcString  the base format of the input files taking an two integers floor and zoom
     * @param baseDestString the base format of the output files
     * @param minFloor      the minimum floor number
     * @param maxFloorInc   the maximum number of floors
     * @param maxZoomInc        the maximum zoom level
     * @param tileWidth      the width of each tile
     * @return returns a String containing all populated tiles in the format "(<floor>,<zoom>,<x>,<y>);"
     */
    private static String splitImages(String srcPath, String destPath, String baseSrcString, String baseDestString, int minFloor, int maxFloorInc, int maxZoomInc, int tileWidth) {
        StringBuilder res = new StringBuilder();
        for (int f = minFloor; f <= maxFloorInc; f++) {
            for (int z = 0; z <= maxZoomInc; z++) {
//                try {
                    //load in each row of tiles into memory before splitting to increase efficiency of memory access
//                    File file = new File(String.format(srcPath + "\\" + baseSrcString, f, z));
//                    ImageReader imageReader = ImageIO.getImageReadersBySuffix("png").next();
//                    ImageInputStream imageInputStream = ImageIO.createImageInputStream(new FileInputStream(file));
//                    imageReader.setInput(imageInputStream, false);
//                    for (int y = 0; y < Math.pow(2, z); y++) {
//                        Rectangle rowRect = new Rectangle(0, y * tileWidth, imageReader.getWidth(0), tileWidth);
//                        ImageReadParam paramRow = imageReader.getDefaultReadParam();
//                        paramRow.setSourceRegion(rowRect);
//                        BufferedImage xRow = imageReader.read(0, paramRow);
//                        for (int x = 0; x < Math.pow(2, z); x++) {
//                            BufferedImage img = xRow.getSubimage(x * tileWidth, 0, tileWidth, tileWidth);
//                            if (!allWhite(img)) {
//                                File destFile = new File(destPath + "\\" + String.format(baseDestString, f, z, x, y));
//                                destFile.getParentFile().mkdirs();
//                                ImageIO.write(img, "png", destFile);
//
//                                //append correct file to allow for return of populated files
//                                res.append(String.format("(%d,%d,%d,%d);", f, z, x, y));
//                            }
//                        }
//                        System.out.print(String.format("\r Splitting floor %d: at line %d/%d", f,  (y + 1), (int)Math.pow(2, z)));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                tileImage(String.format(srcPath + "\\" + baseSrcString, f, z),
                        (destPath + "\\" + String.format(baseDestString, f, z)),
                        z, tileWidth);
            }
        }
        System.out.print("\n");
        return res.toString();
    }

    /**
     * Converts an image stored on disk to a set of tiles also stored on disk
     *
     * @param src path to source file
     * @param destFormat the format of the destination
     * @param zoom
     * @param tileWidth
     */
    private static void tileImage(String src, String destFormat, int zoom, int tileWidth){
        try{
            File file = new File(src);
            ImageReader imageReader = ImageIO.getImageReadersBySuffix("png").next();
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(new FileInputStream(file));
            imageReader.setInput(imageInputStream);
            for(int y = 0; y < Math.pow(2, zoom); y++){
                Rectangle rowRect = new Rectangle(0, y * tileWidth, imageReader.getWidth(0), tileWidth);
                ImageReadParam paramRow = imageReader.getDefaultReadParam();
                paramRow.setSourceRegion(rowRect);
                BufferedImage xRow = imageReader.read(0, paramRow);
                for(int x = 0; x < Math.pow(2, zoom); x++){
                    BufferedImage img = xRow.getSubimage(x * tileWidth, 0, tileWidth, tileWidth);
                    if(!allWhite(img)) {

                        File destFile = new File(String.format(destFormat, x, y));
                        destFile.getParentFile().mkdirs();
                        ImageIO.write(img, "png", destFile);
                    }
                    System.out.print(String.format("\r(x = %d, y = %d)", x, y));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void genBlankTile(String destPath, int TileWidth, Color blank){
        BufferedImage bfi = new BufferedImage(TileWidth, TileWidth, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < TileWidth; i++){
            for(int j = 0; j < TileWidth; j++){
                bfi.setRGB(i, j, blank.getRGB());
            }
        }

        File destFile = new File(destPath + "\\Blank.png");
        destFile.getParentFile().mkdirs();
        try {
            ImageIO.write(bfi, "png", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Combines 4 images of resolution XxY and into one XxY image
     * All input images must be of the same resolution
     * @param A Top Left
     * @param B Top Right
     * @param C Bottom Left
     * @param D Bottom Right
     * @return
     */
    public static BufferedImage combineImageImage(BufferedImage A, BufferedImage B, BufferedImage C, BufferedImage D){
//        BufferedImage res = new BufferedImage(A.getWidth(), B.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        for(int i = 0; i < A.getWidth(); i++){
//            for(int j = 0; j < A.getHeight(); j++){
//                int rgb1 = getPixel(A,B,C,D,i*2,    j*2);
//                int rgb2 = getPixel(A,B,C,D,i*2+1,  j*2);
//                int rgb3 = getPixel(A,B,C,D,i*2,    j*2+1);
//                int rgb4 = getPixel(A,B,C,D,i*2+1,  j*2+1);
//
//                res.setRGB(i,j, averageRGB(rgb1, rgb2, rgb3, rgb4));
//            }
//        }
//
//        return res;

        BufferedImage result = new BufferedImage(A.getWidth(), A.getHeight(), BufferedImage.TYPE_INT_ARGB);

        AffineTransform scale = AffineTransform.getScaleInstance(0.5, 0.5);
        AffineTransformOp scaleOp = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);

        Graphics2D gfx = result.createGraphics();
        gfx.drawImage(A, scaleOp, 0, 0);
        gfx.drawImage(B, scaleOp, A.getWidth() / 2, 0);
        gfx.drawImage(C, scaleOp, 0, A.getHeight() / 2);
        gfx.drawImage(D, scaleOp, A.getWidth() / 2, A.getHeight() / 2);
        gfx.dispose();
        return result;
    }
}
