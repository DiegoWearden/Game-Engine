package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float delta;

    public static int getHeight(){
        return HEIGHT;
    }

    public static int getWidth(){
        return WIDTH;
    }

    public static void createDisplay(){
        ContextAttribs attribs = new ContextAttribs(3,2)
                .withForwardCompatible(true)
                .withProfileCore(true);

        try{
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle(getTitle());
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    /**
     * Returns the time in seconds that has elapsed between the last two frames.
     */
    public static float getFrameTimeSeconds(){
        return delta;
    }

    /**
     * Returns the delta time (in seconds) between the current and previous frame.
     */
    public static float getDeltaTime(){
        return delta;
    }

    public static void closeDisplay(){
        Display.destroy();
    }

    private static String getTitle(){
        String[] phrases = {
                "Just don't break the screen.",
                "Now with 20% more pixels!",
                "It's not procrastination, it's tactical waiting.",
                "Remember to blink occasionally.",
                "Less dangerous than real-life adventures.",
                "Not responsible for lost sleep.",
                "Still a better story than doing laundry.",
                "Voted least likely to cause a zombie apocalypse.",
                "Better than talking to people.",
                "Reality is overrated. Stick with us.",
                "Exercise your thumbs, save the world!",
                "Excellent source of digital Vitamin D.",
                "Because real life doesn't have a pause button.",
                "Gamers do it one life at a time.",
                "Don't worry, virtual calories don't count.",
                "Your couch: the final frontier.",
                "Rated E for Everyone, but L for Laziness.",
                "Does not actually contain dragons.",
                "Warning: may cause joy and laughter.",
                "Just imagine it's educational.",
                "Now gluten-free!",
                "Best enjoyed in pajamas!",
                "Like real life, but more fun!",
                "The game that loves you back.",
                "Helping you avoid eye-contact since 2001.",
                "Still a better story than doing laundry.",
                "100% unicorn-free!",
                "Still safer than hiking!",
                "Less expensive than therapy.",
                "Like a workout, but for your thumbs.",
                "Because real life doesn't have a 'Save' button.",
                "Now with more rainbows!",
                "Not tested on animals, just on humans.",
                "Never knows best.",
                "Secretly a teaching tool in disguise.",
                "Boredom's worst enemy.",
                "Pixels are cheaper than real friends.",
                "Run on caffeine and dreams.",
                "Where productivity goes to die.",
                "Your social life: not included.",
                "Because real life doesn't have a reset button.",
                "Just like outdoor sports, but without sunburn.",
                "We promise this does not help with your maths homework.",
                "It's like reality, but without the consequences.",
                "More fun than cleaning your room.",
                "Warning: May cause laughter and excessive fun.",
                "Where dating sims and social life intersect.",
                "Couch potato approved.",
                "Slightly less challenging than quantum physics.",
                "What chores?",
                "Because calling it work would be wrong.",
                "Cats approve this game.",
                "Now delivering 90% more awesome per square pixel!",
                "Virtual adventures, caffeine sold separately.",
                "Warning: May contain traces of fun.",
                "Does not require batteries or social skills!",
                "More engaging than your last date.",
                "Because life doesn't have a 'Save' button.",
                "100% non-toxic, unless you count fun!",
                "Made with 50% magic, 50% unicorn dust!",
                "Not suitable for zombies.",
                "May cause excessive happiness.",
                "Proudly procrastination approved.",
                "Virtual worlds, real fun.",
                "Introvert's paradise.",
                "NotAllowed yet banned in the real world!",
                "Now with less real-life interaction!",
                "May cause obsession with pixelated characters.",
                "Sleep is for the weak.",
                "Definitely a better investment than your gym membership.",
                "Your boss might not approve...",
                "Less commitment than a pet goldfish.",
                "It's okay, we won't tell anyone you're playing at work!",
                "Disclaimer: Housework not included.",
                "Time enjoyed wasting is not wasted time.",
                "Enjoy the ride, it's faster than a snail!",
                "More fun than watching grass grow!",
                "When reality calls, let it go to voicemail.",
                "Now with more quests than your day job.",
                "Get back to work? Over our pixelated bodies.",
                "Game of the year, according to mom.",
                "Don't try this at home... oh wait, please do.",
                "More fun than sorting socks.",
                "The ultimate adventure in pixel procrastination.",
                "May result in spontaneous laughter.",
                "Less messy than a food fight.",
                "Survive here, and you can survive your in-laws.",
                "Highly addictive, the healthy way.",
                "EXP>HP.",
                "Warning: May contain traces of epicness.",
                "Does not make coffee but will keep you awake.",
                "A break from reality, without the travel expenses.",
                "Pixelated joy on tap.",
                "Goodbye boredom, hello adventure.",
                "A free ticket to Awesometown."
        };
        int phrase = (int) (Math.random() * phrases.length);
        return phrases[phrase];
    }

    private static long getCurrentTime(){
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }
}
