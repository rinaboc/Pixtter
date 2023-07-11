package utils.math;

public class Vec2D {
    private double x;
    private double y;

    public Vec2D(double x, double y){
        this.x = x; this.y = y;
    }

    public Vec2D(Vec2D vec){
        this.x = vec.x;
        this.y = vec.y;
    }

    public void add(Vec2D vec2){
        x += vec2.x();
        y += vec2.y();
    }

    public void subtract(Vec2D vec2){
        x -= vec2.x();
        y -= vec2.y();
    }

    public double length(){
        return Math.sqrt(x*x + y*y);
    }

    public Vec2D multiply(Vec2D vec2){
        return new Vec2D(x * vec2.x, y * vec2.y);
    }

    public double x(){
        return x;
    }

    public double y(){
        return y;
    }

    @Override
    public String toString() {
        return "Vec2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
