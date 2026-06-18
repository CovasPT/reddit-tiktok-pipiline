package main.java.com.covas.model;

public record Resolution(int width, int height) {
    
    public static final Resolution TIKTOK_VERTICAL = new Resolution(1080, 1920);
    public static final Resolution FHD_HORIZONTAL = new Resolution(1920, 1080);

    public Resolution {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensões devem ser estritamente positivas");
        }
    }

    public String toFfmpegSize() {
        return width + "x" + height;
    }

    public double aspectRatio() {
        return (double) width / height;
    }

    public boolean isVertical() {
        return height > width;
    }
}