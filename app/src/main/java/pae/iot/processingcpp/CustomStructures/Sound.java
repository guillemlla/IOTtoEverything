package pae.iot.processingcpp.CustomStructures;

public class Sound {

    public static final int[]   CONFIG_ALARM  = new int[] {1,1,0,0};
    public static final int[]   CONFIG_PIC    = new int[] {0,0,0,1};
    public static final int[]   CONFIG_DATA   = new int[] {0,1,1,0};

    public static final int[]   HEADER              = new int[] {1,1,1,0};
    public static final int[]   HEADER2             = new int[] {0,0,1,1,0};
    public static final int[][] MATRIX_G            = new int[][] {{1,1,0,1},{1,0,1,1},{1,0,0,0},{0,1,1,1},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
    public static final String  SOUND_LOG           = "SoundState";
    public static final String  SOUND_THREAD        = "SoundThread";

}
