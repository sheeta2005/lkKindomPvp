import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.reflect.Method;

public class AutoScriptUI extends JFrame {

    // ====================== 坐标配置 ======================
    public int skill1X = 427, skill1Y = 525;
    public int skill2X = 400, skill2Y = 720;
    public int skill3X = 423, skill3Y = 912;
    public int skill4X = 500, skill4Y = 1090;

    public int chargeX = 130, chargeY = 1300;

    public int pet1X = 500, pet1Y = 475;
    public int pet2X = 450, pet2Y = 615;
    public int pet3X = 460, pet3Y = 760;
    public int pet4X = 490, pet4Y = 890;
    public int pet5X = 600, pet5Y = 1020;

    public int fieldPetX = 1520, fieldPetY = 935;
    public int hpBarCheckX = 155, hpBarCheckY = 113;

    // 匹配界面
    public int startChallengeX = 2199, startChallengeY = 1344;
    public int matchCheckX = 191, matchCheckY = 33;

    // 精灵出战选择界面
    public int selectBlueCheckX = 402, selectBlueCheckY = 20;
    public int firstPetSelectX = 400, firstPetSelectY = 347;
    public int confirmStartX = 1286, confirmStartY = 1325;

    // ✅ 新增：战斗结算界面 坐标
    public int battleEndWhiteCheckX = 1332;   // 正上方白色文字检测点
    public int battleEndWhiteCheckY = 74;
    public int exitButtonX = 1575;           // 退出按钮坐标
    public int exitButtonY = 1324;
    // ======================================================

    // 优势技能绿色 #73c615
    private static final int SKILL_GREEN_R = 0x73;
    private static final int SKILL_GREEN_G = 0xC6;
    private static final int SKILL_GREEN_B = 0x15;

    // 死亡黑血条 #272727
    private static final int HP_BLACK_R = 0x27;
    private static final int HP_BLACK_G = 0x27;
    private static final int HP_BLACK_B = 0x27;

    // 宠物死亡灰色 #76736c
    private static final int PET_DEAD_GRAY_R  = 0x76;
    private static final int PET_DEAD_GRAY_G  = 0x73;
    private static final int PET_DEAD_GRAY_B  = 0x6C;

    // 匹配金色 #DA9822
    private static final int MATCH_GOLD_R = 0xDA;
    private static final int MATCH_GOLD_G = 0x98;
    private static final int MATCH_GOLD_B = 0x22;

    // 出战界面左上角蓝色 #2488ad
    private static final int SELECT_BLUE_R = 0x24;
    private static final int SELECT_BLUE_G = 0x88;
    private static final int SELECT_BLUE_B = 0xAD;

    // ✅ 新增：战斗结算界面 纯白色 #F3EDE0
    private static final int BATTLE_END_WHITE_R = 0xF3;
    private static final int BATTLE_END_WHITE_G = 0xED;
    private static final int BATTLE_END_WHITE_B = 0xE0;

    private static final int COLOR_TOLERANCE = 20;
    private final Random random = new Random();
    private final JTextArea logArea;
    private final JLabel statusLabel;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final NativeClick nativeClick = new NativeClick();

    public AutoScriptUI() {
        setTitle("洛克王国自动化脚本");
        setSize(380, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.DARK_GRAY);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    log("======================================");
                    log("【操作】按下ESC键，脚本已停止运行");
                    log("======================================");
                    running = false;
                    System.exit(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    log("======================================");
                    log(paused ? "【操作】已暂停 → 按空格继续运行" : "【操作】继续运行 → 脚本恢复执行");
                    log("======================================");
                }
            }
            return false;
        });

        statusLabel = new JLabel("准备就绪 | 空格暂停/继续 | ESC退出", SwingConstants.CENTER);
        statusLabel.setForeground(Color.CYAN);
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        add(statusLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.WHITE);
        logArea.setEditable(false);
        add(new JScrollPane(logArea));
        setAlwaysOnTop(true);
        setLocation(20, 20);
    }

    public void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + now() + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private String now() {
        return String.format("%02d:%02d:%02d",
                java.time.LocalTime.now().getHour(),
                java.time.LocalTime.now().getMinute(),
                java.time.LocalTime.now().getSecond());
    }

    private void humanWait(long minMs, long maxMs) throws InterruptedException {
        long ms = minMs + random.nextInt((int) (maxMs - minMs + 1));
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end && running) {
            while (paused && running) Thread.sleep(50);
            Thread.sleep(50);
        }
    }

    private void humanMove(int targetX, int targetY) throws Exception {
        int currentX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        int currentY = (int) MouseInfo.getPointerInfo().getLocation().getY();

        int steps = 8 + random.nextInt(8);
        for (int i = 1; i <= steps; i++) {
            double p = (double) i / steps;
            int x = currentX + (int) ((targetX - currentX) * p);
            int y = currentY + (int) ((targetY - currentY) * p);
            nativeClick.moveMouse(x, y);
            Thread.sleep(12 + random.nextInt(8));
        }
        nativeClick.moveMouse(targetX, targetY);
    }

    private void humanClick(int x, int y) throws Exception {
        int fx = x + random.nextInt(9) - 4;
        int fy = y + random.nextInt(9) - 4;
        humanMove(fx, fy);
        humanWait(120, 240);
        nativeClick.click();
    }

    public void startScript() {
        new Thread(() -> {
            try {
                log("======================================");
                log("【启动】脚本初始化完成，5秒后开始运行");
                log("======================================");
                humanWait(5000, 5500);

                log("======================================");
                log("【运行】脚本开始正常工作");
                log("======================================");

                while (running) {
                    while (paused && running) Thread.sleep(50);
                    if (!running) break;

                    humanWait(400, 900);
                    BufferedImage screen = ScreenUtil.captureFullScreen();

                    // 匹配界面
                    boolean isInMatchScreen = isMatchGoldIcon(screen, matchCheckX, matchCheckY);
                    if (isInMatchScreen) {
                        log("======================================");
                        log("【检测】已进入匹配界面，点击开始挑战");
                        log("======================================");
                        humanClick(startChallengeX, startChallengeY);
                        humanWait(3000, 4000);
                        continue;
                    }

                    // 精灵出战选择界面
                    boolean isInSelectScreen = isSelectBlueIcon(screen, selectBlueCheckX, selectBlueCheckY);
                    if (isInSelectScreen) {
                        log("======================================");
                        log("【检测】进入精灵出战界面，选择一号位");
                        log("======================================");
                        humanClick(firstPetSelectX, firstPetSelectY);
                        humanWait(800, 1200);
                        log("【操作】点击确认首发");
                        humanClick(confirmStartX, confirmStartY);
                        humanWait(3000, 4000);
                        continue;
                    }

                    // ✅ 新增：战斗结算界面（检测正上方白色 → 点退出）
                    boolean isBattleEnd = isBattleEndWhite(screen, battleEndWhiteCheckX, battleEndWhiteCheckY);
                    if (isBattleEnd) {
                        log("======================================");
                        log("【检测】战斗结算界面，点击退出");
                        log("======================================");
                        humanClick(exitButtonX, exitButtonY);
                        humanWait(3000, 4000);
                        continue;
                    }

                    boolean isHpEmpty = isHpBarBlack(screen, hpBarCheckX, hpBarCheckY);
                    if (isHpEmpty) {
                        log("======================================");
                        log("【检测】宠物已死亡，执行换宠");
                        log("======================================");
                        switchToAlivePet(screen);
                        humanWait(3500, 4500);
                        continue;
                    }

                    log("======================================");
                    log("【检测】当前宠物正常，选择技能");
                    log("======================================");

                    int[][] skills = {{skill1X, skill1Y}, {skill2X, skill2Y}, {skill3X, skill3Y}, {skill4X, skill4Y}};
                    for (int i = 0; i < 4; i++) {
                        int checkX = skills[i][0] + 50;
                        int checkY = skills[i][1] - 50;
                        boolean hasGreen = isAdvantageGreen(screen, checkX, checkY);
                        boolean canUse = !isSkillGray(screen, skills[i][0], skills[i][1]);
                        log("【技能】技能" + (i+1) + " → 优势：" + hasGreen + " | 可用：" + canUse);
                    }

                    int bestSkill = findBestAvailableSkill(screen);
                    if (bestSkill == -1) {
                        log("======================================");
                        log("【操作】无可用技能，点击聚能");
                        log("======================================");
                        humanClick(chargeX, chargeY);
                        humanWait(1800, 2400);
                        continue;
                    }

                    log("======================================");
                    log("【操作】使用技能" + (bestSkill + 1));
                    log("======================================");
                    clickSkill(bestSkill);

                    humanWait(4200, 5800);
                }
            } catch (Exception ex) {
                log("======================================");
                log("【异常】脚本出错：" + ex.getMessage());
                log("======================================");
                ex.printStackTrace();
            }
        }).start();
    }

    private int findBestAvailableSkill(BufferedImage screen) {
        int[][] skills = {{skill1X, skill1Y}, {skill2X, skill2Y}, {skill3X, skill3Y}, {skill4X, skill4Y}};
        List<Integer> greenSkills = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int x = skills[i][0] + 50;
            int y = skills[i][1] - 50;
            if (isAdvantageGreen(screen, x, y)) greenSkills.add(i);
        }

        for (int idx : greenSkills) {
            if (!isSkillGray(screen, skills[idx][0], skills[idx][1])) {
                log("【选择】优先优势技能：技能" + (idx+1));
                return idx;
            }
        }

        List<Integer> availableSkills = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (!isSkillGray(screen, skills[i][0], skills[i][1])) {
                availableSkills.add(i);
            }
        }

        if (!availableSkills.isEmpty()) {
            int res = availableSkills.get(random.nextInt(availableSkills.size()));
            log("【选择】随机可用技能：技能" + (res+1));
            return res;
        }
        log("【选择】所有技能不可用，准备聚能");
        return -1;
    }

    private boolean switchToAlivePet(BufferedImage screen) throws Exception {
        int[][] pets = {{pet1X, pet1Y}, {pet2X, pet2Y}, {pet3X, pet3Y}, {pet4X, pet4Y}, {pet5X, pet5Y}};
        log("【换宠】遍历宠物");

        for (int i = 0; i < 5; i++) {
            int x = pets[i][0];
            int y = pets[i][1];
            boolean isPetDead = isPetIconDead(screen, x, y);

            if (!isPetDead) {
                log("【换宠】第" + (i+1) + "只存活");
                humanWait(300, 600);
                humanMove(x, y);
                humanWait(400, 700);
                nativeClick.click();
                humanWait(600, 1000);
                humanMove(fieldPetX, fieldPetY);
                humanWait(400, 700);
                nativeClick.click();
                humanWait(2200, 3000);
                log("======================================");
                log("【换宠】完成");
                log("======================================");
                return true;
            } else {
                log("【换宠】第" + (i+1) + "只死亡，跳过");
                humanWait(200, 400);
            }
        }

        log("======================================");
        log("【换宠】全部死亡");
        log("======================================");
        return false;
    }

    // 匹配金色检测
    private boolean isMatchGoldIcon(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - MATCH_GOLD_R);
            int dg = Math.abs(g - MATCH_GOLD_G);
            int db = Math.abs(b - MATCH_GOLD_B);
            return dr <= COLOR_TOLERANCE && dg <= COLOR_TOLERANCE && db <= COLOR_TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    // 出战界面蓝色检测
    private boolean isSelectBlueIcon(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - SELECT_BLUE_R);
            int dg = Math.abs(g - SELECT_BLUE_G);
            int db = Math.abs(b - SELECT_BLUE_B);
            return dr <= COLOR_TOLERANCE && dg <= COLOR_TOLERANCE && db <= COLOR_TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ 新增：战斗结算白色检测
    private boolean isBattleEndWhite(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - BATTLE_END_WHITE_R);
            int dg = Math.abs(g - BATTLE_END_WHITE_G);
            int db = Math.abs(b - BATTLE_END_WHITE_B);
            return dr <= 15 && dg <= 15 && db <= 15; // 白色更严格
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isHpBarBlack(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - HP_BLACK_R);
            int dg = Math.abs(g - HP_BLACK_G);
            int db = Math.abs(b - HP_BLACK_B);
            return dr <= COLOR_TOLERANCE && dg <= COLOR_TOLERANCE && db <= COLOR_TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPetIconDead(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - PET_DEAD_GRAY_R);
            int dg = Math.abs(g - PET_DEAD_GRAY_G);
            int db = Math.abs(b - PET_DEAD_GRAY_B);
            return dr <= COLOR_TOLERANCE && dg <= COLOR_TOLERANCE && db <= COLOR_TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAdvantageGreen(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int dr = Math.abs(r - SKILL_GREEN_R);
            int dg = Math.abs(g - SKILL_GREEN_G);
            int db = Math.abs(b - SKILL_GREEN_B);
            return dr <= COLOR_TOLERANCE && dg <= COLOR_TOLERANCE && db <= COLOR_TOLERANCE;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSkillGray(BufferedImage img, int x, int y) {
        try {
            int rgb = img.getRGB(x, y);
            int r = rgb >> 16 & 0xff;
            int g = rgb >> 8 & 0xff;
            int b = rgb & 0xff;
            int avg = (r + g + b) / 3;
            return avg < 80;
        } catch (Exception e) {
            return true;
        }
    }

    private void clickSkill(int index) throws Exception {
        int x = switch (index) {
            case 0 -> skill1X;
            case 1 -> skill2X;
            case 2 -> skill3X;
            default -> skill4X;
        };
        int y = switch (index) {
            case 0 -> skill1Y;
            case 1 -> skill2Y;
            case 2 -> skill3Y;
            default -> skill4Y;
        };
        humanClick(x, y);
    }

    public static class NativeClick {
        private static Object robot;
        static {
            try {
                Class<?> robotClass = Class.forName("java.awt.Robot");
                robot = robotClass.getDeclaredConstructor().newInstance();
                Method setAutoDelay = robotClass.getMethod("setAutoDelay", int.class);
                setAutoDelay.invoke(robot, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void moveMouse(int x, int y) throws Exception {
            Method mouseMove = robot.getClass().getMethod("mouseMove", int.class, int.class);
            mouseMove.invoke(robot, x, y);
        }
        public void click() throws Exception {
            Method mousePress = robot.getClass().getMethod("mousePress", int.class);
            Method mouseRelease = robot.getClass().getMethod("mouseRelease", int.class);
            int buttonMask = InputEvent.BUTTON1_MASK;
            mousePress.invoke(robot, buttonMask);
            try { Thread.sleep(50); } catch (InterruptedException e) { }
            mouseRelease.invoke(robot, buttonMask);
        }
    }

    public static void main(String[] args) {
        AutoScriptUI ui = new AutoScriptUI();
        ui.setVisible(true);
        ui.startScript();
    }
}