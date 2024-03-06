package ksnet.sap;

import java.io.File;

class KSFPETimer extends Thread {
    public void run() {
        try {
            while (true) {
                deleteLog();
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String SS_LOG_HOME = null;
    static long SL_LOG_DEL_MILLIS = 0;

    private void deleteLog() {
        String fnm = "deleteLog";

        long cmillis = System.currentTimeMillis();

        if ((cmillis - SL_LOG_DEL_MILLIS) < 86400000L) return;
        if (null == SS_LOG_HOME) {
            SS_LOG_HOME = CUtil.get("USED_DIR");
            if (null == SS_LOG_HOME) {
                LUtil.println("BRCV", fnm + " ERROR : LOG PATH(USED_DIR) SETTING ERROR(1)!!");
                return;
            }
        }

        File dir = new File(SS_LOG_HOME);
        if (dir == null || !dir.isDirectory()) {
            LUtil.println("BRCV", fnm + " ERROR : LOG PATH(USED_DIR) SETTING ERROR(" + SS_LOG_HOME + ")!!");
            return;
        }

        deleteOldFiles(dir, (cmillis - 86400000L), true, false);

    }

    private void deleteOldFiles(File dir, long lastModified, boolean deleteSub, boolean deleteSubDir) {
        File[] fs = dir.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (deleteSub && fs[i].isDirectory()) deleteOldFiles(fs[i], lastModified, true, deleteSubDir);

            if (fs[i].lastModified() < lastModified) {
                if (!fs[i].isDirectory() || deleteSubDir) {
                    boolean rtn = fs[i].delete();
                }
            }
        }
    }
}
