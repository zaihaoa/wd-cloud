package com.xxl.job.core.context;

import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.job.core.util.DateUtil;
import io.netty.handler.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * helper for xxl-job
 *
 * @author xuxueli 2020-11-05
 */
public class XxlJobHelper {

    // ---------------------- base info ----------------------

    /**
     * current JobId
     *
     * @return
     */
    public static long getJobId() {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getJobId();
    }

    /**
     * current JobParam
     *
     * @return
     */
    public static String getJobParam() {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return null;
        }

        return xxlJobContext.getJobParam();
    }

    // ---------------------- for log ----------------------

    /**
     * current JobLogFileName
     *
     * @return
     */
    public static String getJobLogFileName() {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return null;
        }

        return xxlJobContext.getJobLogFileName();
    }

    // ---------------------- for shard ----------------------

    /**
     * current ShardIndex
     *
     * @return
     */
    public static int getShardIndex() {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getShardIndex();
    }

    /**
     * current ShardTotal
     *
     * @return
     */
    public static int getShardTotal() {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return -1;
        }

        return xxlJobContext.getShardTotal();
    }

    // ---------------------- tool for log ----------------------

    private static Logger logger = LoggerFactory.getLogger("xxl-job logger");

    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean log(String appendLogPattern, Object ... appendLogArguments) {
        return log(LogLevel.INFO, appendLogPattern, appendLogArguments);
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean log(LogLevel logLevel, String appendLogPattern, Object ... appendLogArguments) {

        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();

        /*appendLog = appendLogPattern;
        if (appendLogArguments!=null && appendLogArguments.length>0) {
            appendLog = MessageFormat.format(appendLogPattern, appendLogArguments);
        }*/

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog, logLevel);
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean infoLog(String appendLogPattern, Object ... appendLogArguments) {
        return log(LogLevel.INFO, appendLogPattern, appendLogArguments);
    }


    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean warnLog(String appendLogPattern, Object ... appendLogArguments) {
        return log(LogLevel.WARN, appendLogPattern, appendLogArguments);
    }

    /**
     * append log with pattern
     *
     * @param appendLogPattern  like "aaa {} bbb {} ccc"
     * @param appendLogArguments    like "111, true"
     */
    public static boolean errorLog(String appendLogPattern, Object ... appendLogArguments) {
        return log(LogLevel.ERROR, appendLogPattern, appendLogArguments);
    }

    /**
     * append exception stack
     *
     * @param e
     */
    public static boolean errorLog(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog, LogLevel.ERROR);
    }

    /**
     * append log
     *
     * @param callInfo
     * @param appendLog
     */
    private static boolean logDetail(StackTraceElement callInfo, String appendLog, LogLevel logLevel) {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return false;
        }

        /*// "yyyy-MM-dd HH:mm:ss [ClassName]-[MethodName]-[LineNumber]-[ThreadName] log";
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement callInfo = stackTraceElements[1];*/

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DateUtil.formatDateTime(new Date())).append(" ")
                .append("["+ callInfo.getClassName() + "#" + callInfo.getMethodName() +"]").append("-")
                .append("["+ callInfo.getLineNumber() +"]").append("-")
                .append("["+ Thread.currentThread().getName() +"]").append(" ")
                .append(appendLog!=null?appendLog:"");
        String formatAppendLog = stringBuffer.toString();

        // appendlog
        String logFileName = xxlJobContext.getJobLogFileName();

        if (logFileName!=null && logFileName.trim().length()>0) {
            XxlJobFileAppender.appendLog(logFileName, formatAppendLog);

            if (logLevel == null || LogLevel.INFO.equals(logLevel)) {
                logger.info(formatAppendLog);
            } else if (LogLevel.WARN.equals(logLevel)) {
                logger.warn(formatAppendLog);
            } else if (LogLevel.ERROR.equals(logLevel)) {
                logger.error(formatAppendLog);
            } else {
                logger.info(formatAppendLog);
            }

            return true;
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }

    // ---------------------- tool for handleResult ----------------------

    /**
     * handle success
     *
     * @return
     */
    public static boolean handleSuccess(){
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_SUCCESS, null);
    }

    /**
     * handle success with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleSuccess(String handleMsg) {
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_SUCCESS, handleMsg);
    }

    /**
     * handle fail
     *
     * @return
     */
    public static boolean handleFail(){
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_FAIL, null);
    }

    /**
     * handle fail with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleFail(String handleMsg) {
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_FAIL, handleMsg);
    }

    /**
     * handle timeout
     *
     * @return
     */
    public static boolean handleTimeout(){
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_TIMEOUT, null);
    }

    /**
     * handle timeout with log msg
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleTimeout(String handleMsg){
        return handleResult(com.xxl.job.core.context.XxlJobContext.HANDLE_CODE_TIMEOUT, handleMsg);
    }

    /**
     * @param handleCode
     *
     *      200 : success
     *      500 : fail
     *      502 : timeout
     *
     * @param handleMsg
     * @return
     */
    public static boolean handleResult(int handleCode, String handleMsg) {
        com.xxl.job.core.context.XxlJobContext xxlJobContext = com.xxl.job.core.context.XxlJobContext.getXxlJobContext();
        if (xxlJobContext == null) {
            return false;
        }

        xxlJobContext.setHandleCode(handleCode);
        if (handleMsg != null) {
            xxlJobContext.setHandleMsg(handleMsg);
        }
        return true;
    }


}
