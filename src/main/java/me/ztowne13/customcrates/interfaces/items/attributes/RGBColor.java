package me.ztowne13.customcrates.interfaces.items.attributes;

public class RGBColor {
    int r;
    int g;
    int b;

    public RGBColor() {
        this(0, 0, 0);
    }

    public RGBColor(int r, int g, int b) {
        this.r = r;
        this.b = b;
        this.g = g;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
