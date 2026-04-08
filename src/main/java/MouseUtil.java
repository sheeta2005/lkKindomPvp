import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

public class MouseUtil {
    private static Robot robot;
    private static Random random = new Random();

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // 随机移动鼠标（模拟真人）
    public static void moveMouse(int x, int y) {
        try {
            int mx = x + random.nextInt(10) - 5;
            int my = y + random.nextInt(10) - 5;

            PointerInfo p = MouseInfo.getPointerInfo();
            int cx = p.getLocation().x;
            int cy = p.getLocation().y;

            for (int i = 0; i < 20; i++) {
                cx += (mx - cx) / 10;
                cy += (my - cy) / 10;
                robot.mouseMove(cx, cy);
                Thread.sleep(10 + random.nextInt(15));
            }
            robot.mouseMove(mx, my);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 随机延迟点击
    public static void click() {
        try {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(50 + random.nextInt(80));
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(200 + random.nextInt(300));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}