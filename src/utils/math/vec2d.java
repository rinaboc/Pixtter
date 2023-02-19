package utils.math;

public class vec2d {
    public float x, y;

    public vec2d(float x, float y){
        this.x = x;
        this.y = y;
    }

    public vec2d(vec2d v2){
        this.x = v2.x;
        this.y = v2.y;
    }

    public vec2d(float x1, float y1, float x2, float y2){
        this.x = x2 - x1;
        this.y = y2 - y1;
    }

    public void normalize(){
        float l = (float)Math.sqrt(x * x + y * y);
        x /= l; y /= l;
    }

    public void scale(float value){
        x *= value; y *= value;
    }

    public float getLength(){
        return (float)Math.sqrt(x * x + y * y);
    }

    public vec2d multiplyVec(vec2d v2){
        return new vec2d(this.x * v2.x, this.y * v2.y);
    }

    public vec2d divideVec(vec2d v2){
        return new vec2d(this.x / v2.x, this.y / v2.y);
    }

    public vec2d addVec(vec2d v2){
        return new vec2d(this.x + v2.x, this.y + v2.y);
    }

    public vec2d subtractVec(vec2d v2){
        return new vec2d(this.x - v2.x, this.y - v2.y);
    }

    public float dotProduct(vec2d v2){
        return this.x * v2.x + this.y * v2.y;
    }

    public boolean nullVec(){
        return getLength() < 0.01f;
    }
}
