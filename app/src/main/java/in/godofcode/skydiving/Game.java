package in.godofcode.skydiving;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class Game extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder holder;
    public static final int FRAME_TIME=(int)(1000.0/60.0);
    private Paint paint=new Paint();
    private Context context;
    private Thread thread;
    public static boolean ACTIVE=false;
    public static boolean active=false;
    public static int ScreenWidth,ScreenHeight;
    private Tutorial tutorial;
    private float px,py,cx,cy;
    //AnimatedSprite boat;
    Sea sea;
    Typeface t1,t2;
    Bitmap boatB;
    ArrayList<AnimatedSprite> gameObjects;
    public Game(Context context) {
        super(context);
        this.context=context;
        getHolder().addCallback(this);
        //this.setOnTouchListener(this);
        this.ScreenWidth=((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        this.ScreenHeight=((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
        DisplayMetrics metrics=new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((Activity)context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            ScreenWidth=metrics.widthPixels;
            ScreenHeight=metrics.heightPixels;
        }
    }
    public Bitmap fitToScreenBitmap(Bitmap bitmap) {
        //System.out.println(ScreenWidth+"   "+ScreenHeight);
        float ratiow=(float)ScreenWidth/960;
        float ratioh=(float)ScreenHeight/1600;
        return Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*ratiow),(int)(bitmap.getHeight()*ratioh),true);
    }
    public float matchW(float f) {
        float ratiow=(float)ScreenWidth/960;
        return f*ratiow;
    }
    public float matchH(float f) {
        float ratioh=(float)ScreenHeight/1600;
        return f*ratioh;
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus) {
            this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
        else {
            //System.out.println("sssssssss");
            active=false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder=surfaceHolder;
        ACTIVE=true;
        active=true;
        thread=new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
    public void drawSample() {
        paint.setColor(Color.rgb(0,0,250));
        try {
            Canvas canvas=getHolder().lockCanvas();
            canvas.save();
            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
            canvas.restore();
            holder.unlockCanvasAndPost(canvas);
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(tutorial.tutor)
            tutorial.onToach(event);
        return true;
    }

    @Override
    public void run() {
        initial();
        while(ACTIVE) {
            try {
                Thread.sleep(FRAME_TIME);
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
            if(active) {
                update();
                draw();
            }
        }
    }


    public void initial() {
        gameObjects=new ArrayList<>();
        t1=Typeface.createFromAsset(context.getAssets(),"font/font0.ttf");
        t2=Typeface.createFromAsset(context.getAssets(),"font/font1.ttf");
        sea=new Sea(fitToScreenBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.sea)),1,1);
        sea.position=new Vector2(0,ScreenHeight-sea.height);
        sea.start();
        tutorial =new Tutorial();
    }

    class Tutorial {
        public boolean tutor, Tentryb=false,Tentryp=false,landed=false;
        int dow=0;
        AnimatedSprite boat,plane,player,wind,shark;
        public Tutorial() {
            boat=new AnimatedSprite(fitToScreenBitmap(BitmapFactory.decodeResource(Game.this.getResources(),R.drawable.boat)),2,1);
            boat.speed=new Vector2(-5,0);
            boat.position=new Vector2(ScreenWidth,ScreenHeight-boat.height-sea.height/2);
            boat.setCollider(0,boat.height/2,boat.width,boat.height);
            boat.start();
            gameObjects.add(boat);
            plane=new AnimatedSprite(fitToScreenBitmap(BitmapFactory.decodeResource(Game.this.getResources(),R.drawable.plane)),1,1);
            plane.speed=new Vector2(-5,0);
            plane.position=new Vector2(ScreenWidth,(plane.height));
            gameObjects.add(plane);
            tutor=true;
            wind = new AnimatedSprite(fitToScreenBitmap(BitmapFactory.decodeResource(Game.this.getResources(), R.drawable.wind)), 2, 2);
            wind.visible=false;
            gameObjects.add(wind);
            shark=new AnimatedSprite(fitToScreenBitmap(BitmapFactory.decodeResource(Game.this.getResources(), R.drawable.shark1)), 1, 2);
            shark.visible=false;
            shark.speed=new Vector2(0,5);
            gameObjects.add(shark);
        }
        public void update() {
            if(!Tentryb&&boat.position.X<ScreenWidth/2) {
                Tentryb=true;
                boat.speed=new Vector2(-1,0);
            }
            if(!Tentryp&&plane.position.X+plane.width/2<ScreenWidth/2) {
                Tentryp=true;
                plane.speed=new Vector2(-1,0);
                player=new AnimatedSprite(fitToScreenBitmap(BitmapFactory.decodeResource(Game.this.getResources(),R.drawable.player)),3,2);
                player.speed=new Vector2(0,3.5);
                player.position=new Vector2(plane.position.X+plane.width/2,(1.5*plane.height));
                player.setAnimation(new int[] {0},200);
                player.start();
                gameObjects.add(player);
            }
            if(player!=null) {
                if(player.position.Y+player.height>=boat.position.Y-matchH(5)&&player.currentFrame==1&&(player.position.X+player.width<boat.position.X||player.position.X>boat.position.X+boat.width)) {
                    player.setAnimation(new int[]{2, 3}, 200);
                }
                if(!landed&&boat.isCollideWith(player.getCollider())) {
                    player.speed=boat.speed;
                    player.setAnimation(new int[]{4},200);
                    landed=true;
                }
                else if(!landed&&sea.isCollideWith(player.getCollider())) {
                    landed=true;
                    player.visible=false;
                    shark.position.X=player.position.X;
                    shark.position.Y=player.position.Y+player.height/2;
                    shark.visible=true;
                }
            }
        }
        public void onToach(MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN) {
                //for(int i=0;i<gameObjects.size();i++)
                    //System.out.println(gameObjects.get(i));
                px=cx=event.getX();
                py=cy=event.getY();
                dow=0;
                shark.visible=false;
            }
            else if(event.getAction()==MotionEvent.ACTION_MOVE&&dow<=7) {
                //System.out.println("move");
                cx=event.getX();
                cy=event.getY();
                float dx=cx-px;
                float dy=cy-py;
                if(Math.abs(dx)>Math.abs(dy)) {
                    if(player!=null) {
                        if(dx<0) {
                            player.position.X += matchW(-3);
                            wind.position = new Vector2(player.position.X, player.position.Y + (player.height / 2) - wind.height / 2);
                            wind.currentFrame = 1;
                            wind.visible=true;
                        }
                        else if(dx>0) {
                            player.position.X += matchW(3);
                            wind.position = new Vector2(player.position.X, player.position.Y + (player.height / 2) - wind.height / 2);
                            wind.currentFrame = 0;
                            wind.visible=true;
                        }
                    }
                }
                else {
                    if(dy<0) {
                        player.position.Y += matchH(-3);
                        wind.position = new Vector2(player.position.X, player.position.Y + (player.height / 2) - wind.height / 2);
                        wind.currentFrame = 2;
                        wind.visible=true;
                    }
                    else if(dy>0) {
                        player.position.Y += matchH(3);
                        wind.position = new Vector2(player.position.X, player.position.Y + (player.height / 2) - wind.height / 2);
                        wind.currentFrame = 3;
                        wind.visible=true;
                    }
                }
                px=cx;
                py=cy;
                dow++;
            }
            else if(event.getAction()==MotionEvent.ACTION_UP) {
                wind.visible=false;
            }
            if(player!=null) {
                if(player.currentFrame==0) {
                    player.speed=new Vector2(0,2);
                    player.setAnimation(new int[]{1}, 200);
                }
            }
        }
    }
    public void update() {
        tutorial.update();
        for(AnimatedSprite as : gameObjects) {
            as.move();
        }
    }
    public void draw()  {
        drawBack();
    }

    public void drawBack() {
        try {
            Canvas canvas=getHolder().lockCanvas();
            canvas.save();
            paint.setColor(Color.rgb(210,210,210));
            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
            paint.setColor(Color.rgb(10,10,10));
            /*paint.setTypeface(t1);
            paint.setTextSize(50);
            canvas.drawText("Hello World",150,150,paint);
            paint.setTypeface(t2);
            canvas.drawText("Hello World",150,450,paint);*/
            for(AnimatedSprite as : gameObjects) {
                as.draw(canvas,paint);
            }
            sea.draw(canvas,paint);
            canvas.restore();
            holder.unlockCanvasAndPost(canvas);
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
    }
}
