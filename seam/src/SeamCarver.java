import edu.princeton.cs.algs4.*;
import java.lang.Math;
import java.awt.Color;
 
public class SeamCarver {
 
    private Picture picture;
 
    private static final double maxEnergy = 1000;
 
    private boolean isValidPixel (int x, int y) {
	return (x >= 0 || x < width() || y >= 0 || y < height());
    }
 
    private boolean isBorderPixel (int x, int y) {
	return (x == 0 || x == width() - 1 || y == 0 || y == height() - 1);
    }
 
    /**
     * Square of the gradient Δ^2(x, y) = R(x, y)^2 + G(x, y)^2 + B(x, y)^2
     *
     * @param a
     * @param b
     * @return The square of the gradient
     */
    private double gradient(Color a, Color b) {
        int red = a.getRed() - b.getRed();
        int green = a.getGreen() - b.getGreen();
        int blue = a.getBlue() - b.getBlue();
        return red * red + green * green + blue * blue;
    }

    private void relax(int from, int to, double [] energy, double [] distTo, int [] edgeTo) {
        if (distTo[to] > distTo[from] + energy[to]) {
            distTo[to] = distTo[from] + energy[to];
            edgeTo[to] = from;
        }
    }

    //go backward from the last row to fin
    private int[] verticalSeam(int end, int [] edgeTo) {
        int[] result = new int[height()];
        int tmp = end;

        while (tmp >= 0) {
            result[tmp / width()] = tmp % width();
            tmp = edgeTo[tmp];
        }

        return result;
    }

    private void checkSeamValidity(int[] seam) {
        if (width() <= 1) {
            throw new IllegalArgumentException("The width of the picture must be greatern than 1");
        }
        if (seam.length != height()) {
            throw new IllegalArgumentException("The seam size must be greater than 1.");
        }

        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("The seam is not valid.");
            }
        }
    }

    private void transpose() {
	Picture transpose = new Picture(picture.height(), picture.width());

        for (int col = 0; col < transpose.width(); col++) {
            for (int row = 0; row < transpose.height(); row++) {
                transpose.set(col, row, picture.get(row, col));
            }
        }
	picture = transpose;
    }

    private int position(int col, int row) {
        return width() * row + col;
    }
   
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
	this.picture = picture;
    }
 
    // current picture
    public Picture picture() {
	return picture;
    }
    // width of current picture
    public int width() {
	return picture.width();
    }
    // height of current picture
    public int height() {
	return picture.height();
    }
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
              
	if (!isValidPixel(x, y)) {
	    throw new IndexOutOfBoundsException();
	}
 
	if (isBorderPixel(x, y)) {
	    return maxEnergy;
	}
	
	double xDiff = gradient(picture.get(x - 1, y), picture.get(x + 1, y));
        double yDiff = gradient(picture.get(x, y - 1), picture.get(x, y + 1));
        return Math.sqrt(xDiff + yDiff);
    }
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
	transpose();
	int [] seam = findVerticalSeam();
	transpose();
	return seam;
    }
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
	int size = width() * height();

        double [] energy = new double[size]; //weights
        double [] distTo = new double[size]; //distances
        int [] edgeTo = new int[size];  //paths (parents)
        int p;

	//initialize distances and energies
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                p = position(col, row);

                distTo[p] = (row == 0) ? 0 : Double.POSITIVE_INFINITY;
		
                energy[p] = energy(col, row);
                edgeTo[p] = -1;
            }
        }

        for (int row = 0; row < height() - 1; row++) {
            for (int col = 0; col < width(); col++) {
                p = position(col, row);

                if (col > 0) {
                    relax(p, position(col-1, row + 1), energy, distTo, edgeTo);
                }

                relax(p, position(col, row + 1), energy, distTo, edgeTo);

                if (col < width() - 1) {
                    relax(p, position(col+1, row + 1), energy, distTo, edgeTo);
                }
            }
        }

        double min = Double.POSITIVE_INFINITY;
        int end = 0;

        for (int col = 0; col < width(); col++) {
            if (distTo[position(col, height() - 1)] < min) {
                min = distTo[position(col, height()-1)];
                end = position(col, height() - 1);
            }
        }
        
        return verticalSeam(end, edgeTo);
    }
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
	transpose();
	removeVerticalSeam(seam);
        transpose();
    }
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {

	checkSeamValidity(seam);

	Picture result = new Picture(width() - 1, height());

        for (int row = 0; row < height(); row++) {
	    int res_col = 0;
            for (int col = 0; col < width(); col++) {
                if (col != seam[row]) {
                    result.set(res_col, row, picture.get(col, row));
		    res_col++;
                }
            }
        }

        picture = result;
    }
}
