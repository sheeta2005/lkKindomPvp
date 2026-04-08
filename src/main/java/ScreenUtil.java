import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenUtil {
    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // 全屏截图
    public static BufferedImage captureFullScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        return robot.createScreenCapture(screenRect);
    }

    // 区域截图
    public static BufferedImage captureArea(int x, int y, int w, int h) {
        Rectangle rect = new Rectangle(x, y, w, h);
        return robot.createScreenCapture(rect);
    }
}