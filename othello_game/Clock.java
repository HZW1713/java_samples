public class Clock implements Runnable {
    private OthelloView othelloView;
    private int time;
    private boolean flag = true;

    public Clock(OthelloView ov ,int sec) {
        othelloView = ov;
        this.time = sec;
    }

    @Override
    public void run() {
        int minute = time / 60;
        int second = time % 60;
        if (second < 10) {
            othelloView.setTime(minute + ":0" + second);
        } else {
            othelloView.setTime(minute + ":" + second);
        }

        while (flag && time != 0) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) { }
            time--;
            minute = time / 60;
            second = time % 60;
            if (time%60 < 10) {
                othelloView.setTime(minute + ":0" + second);
            } else {
                othelloView.setTime(minute + ":" + second);
            }
        }

        if (time == 0) {
            othelloView.notifyTimeOver();
        }
    }

    public void stopTime() {
        flag = false;
    }
    
}
