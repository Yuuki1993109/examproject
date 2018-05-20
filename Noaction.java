import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;

public class Noaction {
    
    int subtimex = 0;
    int subtimey = 0; //直前の座標保存
    int timecount = 0; //経過した時間カウント

    /** コンストラクタ */
    public Noaction() throws IOException, AWTException {
        

        // タスクトレイアイコン
        Image image = ImageIO.read(
                getClass().getResourceAsStream("icon.png"));
        final TrayIcon icon = new TrayIcon(image);
    
     
        // タスクトレイに格納
        SystemTray.getSystemTray().add(icon);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                Point point = pointerInfo.getLocation();
                int x = point.x;
                int y = point.y;
                
                if(x==subtimex && y==subtimey){
                    timecount ++;
                    if(timecount == 18){
                        icon.displayMessage("タイマー","マウス無操作時間が3分を超えました", TrayIcon.MessageType.INFO);
                        timecount = 0;
                    }
                }else{
                    timecount = 0;
                }   
                subtimex = x;
                subtimey = y;
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1 * 10 * 1000); // 10秒ごと
    }
    /** メインメソッド */ 
    public static void main(String[] args) throws Exception {
        new Noaction();
    }
}