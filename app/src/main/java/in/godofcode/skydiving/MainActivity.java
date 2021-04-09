package in.godofcode.skydiving;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class MainActivity extends Activity {
    Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game=new Game(this);
        setContentView(game);
    }
}
