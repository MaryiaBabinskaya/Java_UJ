package uj.wmii.pwj.introduction;

public class QuadraticEquation {

    public double[] findRoots(double a, double b, double c) {
        double d = Math.pow(b,2) - 4 * a * c;
        if (d > 0) {
            double x1 = (- b + Math.sqrt(d)) / (2 * a);
            double x2 = (- b - Math.sqrt(d)) / (2 * a);
            double[] tab = {x1, x2};
            return tab;
        }
        else if (d == 0) {
            double x1 = (- b) / (2 * a);
            double[] tab = {x1};
            return tab;
        }
        else {
            double[] tab = new double[0];
            return tab;
        }
    }

}

