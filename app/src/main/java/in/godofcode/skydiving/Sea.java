package in.godofcode.skydiving;

import android.graphics.Bitmap;

public class Sea extends AnimatedSprite {
    public Sea(Bitmap bitmap, int rows, int columns) {
        super(bitmap, rows, columns);
        this.speed=new Vector2(-2,0);
    }

    @Override
    public void run() {
        while (Game.ACTIVE) {
            try {
                Thread.sleep(timer);
                if(Game.active) {
                    if(this.position.X+this.bitmap.getWidth()<=Game.ScreenWidth)
                    {
                        this.speed = new Vector2(2, 0);
                    }
                    else if(this.position.X>=0)
                    {
                        this.speed = new Vector2(-2, 0);
                    }
                    this.move();
                }
            }
            catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }
}
