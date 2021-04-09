package in.godofcode.skydiving;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class AnimatedSprite implements Runnable{
    public Bitmap bitmap;
    public Vector2 position;
    public Vector2 speed;
    public int rows,columns,width,height,currentFrame,totalFrame;
    protected Thread thread;
    protected int timer,index=0;
    protected int[] anime;
    private Rect collider;
    public boolean visible;
    public AnimatedSprite(Bitmap bitmap,int rows,int columns) {
        this.bitmap = bitmap;
        this.rows = rows;
        this.columns = columns;
        currentFrame = 0;
        totalFrame = this.rows * this.columns;
        this.width = bitmap.getWidth() / this.columns;
        this.height = bitmap.getHeight() / this.rows;
        position = new Vector2(0, 0);
        speed = new Vector2(0, 0);
        thread=new Thread(this);
        timer=100;
        collider=new Rect(0, 0, width, height);
        anime=new int[totalFrame];
        visible=true;
        //System.out.println(bitmap.getWidth()+"  "+bitmap.getHeight());
        for(int i=0;i<totalFrame;i++)
            anime[i]=i;
    }
    public void setAnimation(int[] animation,int timer) {
        index=0;
        this.anime=animation;
        this.timer=timer;
    }
    public void start() {
        thread.start();
    }
    public Rect getCollider()
    {
        Rect rectangle = new Rect((int)position.X+this.collider.left, (int)position.Y+this.collider.top, (int)position.X+this.collider.right, (int)position.Y+this.collider.bottom);
        return rectangle;
    }
    public boolean isCollideWith(Rect collider)
    {
        Rect rectangle = new Rect((int)position.X+this.collider.left, (int)position.Y+this.collider.top, (int)position.X+this.collider.right, (int)position.Y+this.collider.bottom);
        if (rectangle.intersect(collider))
            return true;
        return false;
    }
    public void setCollider(int left,int top,int right,int bottom) {
        this.collider=new Rect(left,top,right,bottom);
    }
    public void draw(Canvas canvas, Paint paint) {
        if(!visible)
            return;
        int w=bitmap.getWidth()/columns;
        int h=bitmap.getHeight()/rows;
        int r=currentFrame/columns;
        int c=currentFrame%columns;
        Bitmap temp=Bitmap.createBitmap(bitmap,w*c,h*r,w,h);
        canvas.drawBitmap(temp,(float)position.X,(float)position.Y,paint);
    }
    public void move()
    {
        position.X += speed.X;
        position.Y += speed.Y;
    }

    @Override
    public void run() {
        while (Game.ACTIVE) {
            try {
                Thread.sleep(timer);
                if(Game.active) {
                    if(index<anime.length)
                        this.currentFrame=anime[index++];
                    else {
                        index=0;
                        this.currentFrame=anime[index++];
                    }
                }
            }
            catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }
}
