package pae.iot.processingcpp.CustomStructures;

public final class Protocol {

    public final static int DEVICE_TX_MODE = 0x0A;
    public final static int DEVICE_RX_MODE = 0x0B;

    public final static int IDLE          = 0x01;

    public final static int START_ID      = 0x02;
    public final static int WAITING_ID    = 0x03;
    public final static int COMPLETE_ID   = 0x04;

    public final static int START_DATA    = 0x05;
    public final static int WAITING_DATA  = 0x06;
    public final static int COMPLETE_DATA = 0x07;
    public final static int NEXT_PACKET   = 0x08;
    public final static int STOP_REQUEST  = 0x09;

    public final static int FLASH_ID      = 0x10;
    public final static int FLASH_DATA    = 0x20;
    public final static int FLASH_PACKET  = 0x40;
    public final static int FLASH_STOP    = 0x50;

    public final static int FLASH_ID_SEQ    = 1;
    public final static int FLASH_DATA_SEQ  = 2;
    public final static int FLASH_STOP_SEQ  = 3;

    public final static String IP_SERVER = "10.0.100.202";

    public final static int ACTIVITY_INTENT = 0;
    public final static String FINAL_STRING_IDENTIFIER = "final_result";

    public final static int DELAY = 100;

    public final static String FLASH_TAG    = "FlashConfig";
    public final static String STATE_TAG    = "FlashState";
    public final static String RECEIVER_TAG = "Receiver";
    public final static String OPENCV_TAG   = "OpenCV";
}
